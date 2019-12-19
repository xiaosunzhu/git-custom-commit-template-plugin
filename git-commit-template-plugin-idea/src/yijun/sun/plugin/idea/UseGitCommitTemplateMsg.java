package yijun.sun.plugin.idea;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.FileUtils;

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
		if (!file.exists()) {
			try {
				FileUtils.writeStringToFile(file, message);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			try {
				message = FileUtils.readFileToString(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		commitMessage.setCommitMessage(message);
	}

	private String getDefaultMessage() {
		return "[type] subject\n"
			+ "# Please remove comments. \n"
			+ "# type: feat(new feature) / oss(oss bug) / bts(bts bug) / docs / style(file style change) / refactor / test(only test code change) / chore(build or tool)\n"
			+ "\n"
			+ "Issues: \n"
			+ "Description: \n"
			+ "\n"
			+ "RelateModule: ";
	}
}
