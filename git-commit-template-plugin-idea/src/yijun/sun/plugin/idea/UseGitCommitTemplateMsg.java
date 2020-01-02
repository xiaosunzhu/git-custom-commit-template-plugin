package yijun.sun.plugin.idea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.CommitMessageI;
import com.intellij.openapi.vcs.VcsDataKeys;

public class UseGitCommitTemplateMsg extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		CommitMessageI commitMessage = e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL);
		if (Objects.isNull(commitMessage)) {
			System.err.println("commitMessage null");
			return;
		}

		String message = getDefaultMessage();
		Project project = e.getProject();
		if (Objects.isNull(project)) {
			commitMessage.setCommitMessage(message);
			System.err.println("project is null");
			return;
		}
		File file = new File(project.getBasePath(), "git-message-template.txt");
		try {
			if (!file.exists()) {
				boolean createResult = file.createNewFile();
				if (createResult) {
					FileWriter writer = null;
					try {
						writer = new FileWriter(file);
						writer.write(message);
						writer.flush();
					} catch (Throwable ex) {
						ex.printStackTrace();
						if (writer != null) {
							writer.close();
						}
					}
				}
			} else if (file.isFile() && file.canRead()) {
				FileReader reader = null;
				BufferedReader buffer = null;
				try {
					reader = new FileReader(file);
					buffer = new BufferedReader(reader);
					final StringBuilder s = new StringBuilder();
					buffer.lines().forEach(l -> s.append(l).append("\n"));
					message = s.toString();
				} catch (Throwable ex) {
					if (buffer != null) {
						buffer.close();
					}
					if (reader != null) {
						reader.close();
					}
					ex.printStackTrace();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		commitMessage.setCommitMessage(message);
	}

	private String getDefaultMessage() {
		return "[type] subject\n"
			+ "# Please remove comments. \n"
			+ "# type: feat(new feature) / fix / docs / style(file style change) / refactor / test(only test code change) / chore(build or tool)\n"
			+ "\n"
			+ "Issues: \n"
			+ "Description: \n"
			+ "\n"
			+ "RelateModule: ";
	}
}
