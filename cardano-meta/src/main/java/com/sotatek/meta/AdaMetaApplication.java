package com.sotatek.meta;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.sotatek.meta.document.MetaData;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SpringBootApplication
@EnableScheduling
public class AdaMetaApplication extends SpringBootServletInitializer {

	private static final Logger LOGGER = Logger.getLogger(AdaMetaApplication.class);

	@Value("${git.local.repository.path}")
	private String gitLocalRepoPath;
	@Value("${git.repository.url}")
	private String gitRepoUrl;

	public static void main(String[] args) {
		SpringApplication.run(AdaMetaApplication.class, args);
	}

	@Scheduled(cron = "0 0/3 * * * *")
	private void checkRepoFunc() throws IOException, GitAPIException{
		LOGGER.info("Current repo path: " + gitLocalRepoPath);
		File localRepoDir = new File(gitLocalRepoPath);
		if (!localRepoDir.exists()){
			localRepoDir.mkdir();
		}
		TextProgressMonitor consoleProgressMonitor = new TextProgressMonitor(new PrintWriter(System.out));
		if(localRepoDir.listFiles().length > 0) {
			// was cloned before
			LOGGER.info("Call pull request to check status of cardano token registry repository !");
			Git git = Git.open(localRepoDir);
			Repository exsistRepo = git.getRepository();
			ObjectId oldHead = exsistRepo.resolve("HEAD^{tree}");
			PullResult pullResult = git.pull().setProgressMonitor(consoleProgressMonitor).setRemote("origin")
										.setRemoteBranchName("main").call();
			if (pullResult.isSuccessful()) {
				LOGGER.info("Pull Successfull");
				ObjectId newHead = exsistRepo.resolve("HEAD^{tree}");
				ObjectReader reader = exsistRepo.newObjectReader();
				CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
				oldTreeIter.reset(reader, oldHead);
				CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
				newTreeIter.reset(reader, newHead);
				List<DiffEntry> diffs = git.diff()
						.setNewTree(newTreeIter)
						.setOldTree(oldTreeIter)
						.call();
				if (diffs.size() > 0) {
					LOGGER.info("Diffs size: " + diffs.size());
					List<String> listChangesFile = new ArrayList<>();
					List<String> listDeletedFile = new ArrayList<>();
					String mappingsPath = "mappings.*json";
					Pattern pattern = Pattern.compile(mappingsPath);
					for (DiffEntry diff : diffs) {
						if(diff.getChangeType().equals(DiffEntry.ChangeType.DELETE)) {
							LOGGER.info("diff.getOldPath(): " + diff.getOldPath());
							if(pattern.matcher( diff.getOldPath() ).matches()) {
								listDeletedFile.add(diff.getOldPath());
							}
						} else {
							LOGGER.info("diff.getNewPath(): " + diff.getNewPath());
							LOGGER.info("pattern.matcher( diff.getNewPath() ).matches(): " + pattern.matcher( diff.getNewPath() ).matches());
							if(pattern.matcher( diff.getNewPath() ).matches()) {
								listChangesFile.add(diff.getNewPath());
							}
						}
					}
					if (listChangesFile.size() > 0) {
						handleAndSaveAllRepoChanges(listChangesFile);
					}
					if (listDeletedFile.size() > 0) {
						handleAndDeleteFileRemoved(listChangesFile);
					}
				} else {
					LOGGER.info("Everything up to date !");
				}
			} else {
				LOGGER.error("Pull Failed!");
			}
		} else {
			// first time clone repo
			LOGGER.info("\n>>> Cloning repository\n");
			Repository repoClone = Git.cloneRepository().setProgressMonitor(consoleProgressMonitor).setDirectory(localRepoDir)
					.setURI(gitRepoUrl).call().getRepository();
			LOGGER.info("\n>>> Cloning done !\n");
		}
	}

	public void handleAndSaveAllRepoChanges(List<String> listChangesFile) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<MetaData> metaDataList = new ArrayList<>();
		for (String changeFile : listChangesFile) {
			try {
				InputStream is = MetaData.class.getResourceAsStream("../" + changeFile);
				MetaData metaData = mapper.readValue(is, MetaData.class);
				System.out.println(metaData.toString());
				metaDataList.add(metaData);
			} catch (Exception ex) {
				LOGGER.error("Parse JSON Failed!" , ex);
				continue;
			}
		}
	}
	public void handleAndDeleteFileRemoved(List<String> listDeletedFile) {

	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(AdaMetaApplication.class);
	}

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
		mapper
				.registerModule(new ParameterNamesModule())
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}
}
