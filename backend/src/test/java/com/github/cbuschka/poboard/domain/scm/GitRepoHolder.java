package com.github.cbuschka.poboard.domain.scm;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class GitRepoHolder
{
	private File repoDir;

	private Git git;

	private List<ObjectId> objectIds;

	public GitRepoHolder()
	{
	}

	public ObjectId getCommitish(int index)
	{
		return objectIds.get(index);
	}

	public GitRepoHolder newRepo() throws IOException, GitAPIException
	{
		this.repoDir = Files.createTempDirectory("repo").toFile();
		Git.init().setDirectory(this.repoDir).setBare(false).setInitialBranch("main").call();
		Repository repo = FileRepositoryBuilder.create(new File(this.repoDir, ".git"));
		this.git = new Git(repo);
		this.objectIds = new ArrayList<>();
		return this;
	}

	public GitRepoHolder add(String path, String content, String comment) throws IOException, GitAPIException
	{
		File file = new File(repoDir, path);
		Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
		String relPath = file.getAbsolutePath().substring(this.repoDir.getAbsolutePath().length() + 1);
		git.add()
				.addFilepattern(relPath)
				.call();
		git.commit()
				.setMessage(comment)
				.setAuthor("generator", "generator@example.com")
				.call();
		assertThatIsClean();
		this.objectIds.add(getHead());

		return this;
	}

	private void assertThatIsClean() throws GitAPIException
	{
		if (!git.status().call().isClean())
		{
			throw new IllegalStateException("Not clean after commit!");
		}
	}

	public ObjectId getHead() throws IOException
	{
		return resolve(Constants.HEAD);
	}

	public ObjectId resolve(String name) throws IOException
	{
		Repository repo = this.git.getRepository();
		return repo.resolve(name);
	}


	public Git getGit()
	{
		return git;
	}

	public void destroy()
	{
		if (this.repoDir != null)
		{
			deleteDirectory(this.repoDir);
		}
	}

	private void deleteDirectory(File directoryToBeDeleted)
	{
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null)
		{
			for (File file : allContents)
			{
				deleteDirectory(file);
			}
		}
		directoryToBeDeleted.delete();
	}

	public GitRepoHolder remove(String path, String comment) throws IOException, GitAPIException
	{
		File file = new File(repoDir, path);
		Files.delete(file.toPath());
		String relPath = file.getAbsolutePath().substring(this.repoDir.getAbsolutePath().length() + 1);
		git.rm().addFilepattern(relPath)
				.call();
		git.commit()
				.setMessage(comment)
				.setAuthor("generator", "generator@example.com")
				.call();
		assertThatIsClean();
		this.objectIds.add(getHead());

		return this;
	}
}
