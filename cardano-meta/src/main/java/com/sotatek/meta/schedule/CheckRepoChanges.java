package com.sotatek.meta.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotatek.meta.AdaMetaApplication;
import com.sotatek.meta.document.MetaData;
import com.sotatek.meta.repository.MetaDataRepository;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CheckRepoChanges {

    private static final Logger LOGGER = Logger.getLogger(CheckRepoChanges.class);
    @Value("${git.local.repository.path}")
    private String gitLocalRepoPath;
    @Value("${git.repository.url}")
    private String gitRepoUrl;

    @Autowired
    MetaDataRepository metaDataRepository;

    @Scheduled(cron = "0 0/1 * * * *")
    private void checkRepoFunc() throws IOException, GitAPIException {
        LOGGER.info("Current repo path: " + gitLocalRepoPath);
        File localRepoDir = new File(gitLocalRepoPath);
        ObjectMapper mapper = new ObjectMapper();
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
                    .setRemoteBranchName("master").call();
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
                    String mappingsPath = ".*/mappings/(.*)\\.json";
                    Pattern pattern = Pattern.compile(mappingsPath);
                    Matcher matcher;
                    for (DiffEntry diff : diffs) {
                        if(diff.getChangeType().equals(DiffEntry.ChangeType.DELETE)) {
                            LOGGER.info("diff.getOldPath(): " + diff.getOldPath());
                            matcher = pattern.matcher( diff.getOldPath() );
                            if(matcher.matches()) {
                                listDeletedFile.add(matcher.group(1));
                            }
                        } else {
                            LOGGER.info("diff.getNewPath(): " + diff.getNewPath());
                            matcher = pattern.matcher( diff.getNewPath() );
                            if(matcher.matches()) {
                                listChangesFile.add(diff.getNewPath());
                            }
                        }
                    }

                    System.out.println(listChangesFile.size());
                    if (listChangesFile.size() > 0) {
                        List<MetaData> metaDataChangedList = new ArrayList<>();
                        for (String changeFile : listChangesFile) {
                            try {
                                System.out.println(changeFile);
                                metaDataChangedList.add(mapper.readValue(new File("C:/Users/ThinkPad/Desktop/github/ada-meta/" + changeFile), MetaData.class));
                            } catch (Exception ex) {
                                LOGGER.error("Parse JSON Failed!" , ex);
                                continue;
                            }
                        }
                        if (metaDataChangedList.size() > 0) {
                            try {
                                metaDataRepository.saveAll(metaDataChangedList);
                            } catch (Exception ex) {
                                LOGGER.error("Save all changes failed!" , ex);
                            }
                        }
                    }
                    if (listDeletedFile.size() > 0) {
                        List<MetaData> metaDataRemovedList = new ArrayList<>();
                        for (String deleteFile : listDeletedFile) {
                            try {
                                metaDataRemovedList.add((new MetaData(deleteFile)));
                            } catch (Exception ex) {
                                LOGGER.error("Parse JSON Failed!" , ex);
                                continue;
                            }
                        }
                        if (metaDataRemovedList.size() > 0) {
                            try {
                                metaDataRepository.deleteAll(metaDataRemovedList);
                            } catch (Exception ex) {
                                LOGGER.error("Delete all remove file failed!" , ex);
                            }
                        }
                    }
                } else {
                    LOGGER.info("Everything up to date !");
                }
            } else {
                LOGGER.error("Pull Failed!");
            }
        } else {
            // first time clone repo
            try {
                LOGGER.info("\n>>> Cloning repository\n");
                Repository repoClone = Git.cloneRepository().setProgressMonitor(consoleProgressMonitor).setDirectory(localRepoDir)
                        .setURI(gitRepoUrl).call().getRepository();
                LOGGER.info("\n>>> Cloning done !\n");
                localRepoDir = new File(gitLocalRepoPath + "/mappings");
                if(localRepoDir.listFiles().length > 0) {
                    List<MetaData> metaDataClonedList = new ArrayList<>();
                    for (File jsonFile : localRepoDir.listFiles()){
                        try {
                            metaDataClonedList.add(mapper.readValue(jsonFile, MetaData.class));
                        } catch (Exception ex) {
                            LOGGER.error("Parse failed, maybe incorrect file input!" , ex);
                            continue;
                        }
                    }
                    if (metaDataClonedList.size() > 0) {
                        try {
                            metaDataRepository.saveAll(metaDataClonedList);
                        } catch (Exception ex) {
                            LOGGER.error("Save all clone file failed!" , ex);
                        }
                    }
                } else {
                    LOGGER.info("Empty mappings folder from Git repositoty !");
                }
            } catch (GitAPIException ex) {
                LOGGER.error("Clone Failed!" , ex);
            }
        }
    }
}
