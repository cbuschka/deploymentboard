package com.github.cbuschka.deploymentboard.domain.scm;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

public interface GitChangeCollector
{
	boolean handles(ChangeDetectionAlgorithm algorithm);

	List<Change> collectChanges(Git git, String commitish, String optionalEndCommitish) throws Exception;
}
