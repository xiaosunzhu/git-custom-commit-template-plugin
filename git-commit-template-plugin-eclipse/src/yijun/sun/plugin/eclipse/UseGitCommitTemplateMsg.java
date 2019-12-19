package yijun.sun.plugin.eclipse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Consumer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.egit.ui.ICommitMessageProvider;

public class UseGitCommitTemplateMsg implements ICommitMessageProvider {

	@Override
	public String getMessage(IResource[] arg0) {
		IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		if (rootPath == null) {
			return "";
		}
		String dir = rootPath.toString();

		String message = getDefaultMessage();
		File file = new File(dir, "git-message-template.txt");
		try {
			if (!file.exists()) {
				boolean createResult = file.createNewFile();
				if (createResult) {
					FileWriter writer = null;
					try {
						writer = new FileWriter(file);
						writer.write(message);
						writer.flush();
					} catch (Throwable e) {
						e.printStackTrace();
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
					buffer.lines().forEach(new Consumer<String>() {
						@Override
						public void accept(String l) {
							s.append(l).append("\n");
						}
					});
					message = s.toString();
				} catch (Throwable e) {
					if (buffer != null) {
						buffer.close();
					}
					if (reader != null) {
						reader.close();
					}
					e.printStackTrace();
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return message;
	}

	private String getDefaultMessage() {
		return "[type] subject\n" + "# Please remove comments. \n"
				+ "# type: feat(new feature) / bug / docs / style(file style change) / refactor / test(only test code change) / chore(build or tool)\n"
				+ "\n" + "Issues: \n" + "Description: \n" + "\n" + "RelateModule: ";
	}
}
