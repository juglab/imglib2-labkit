package net.imglib2.atlas.actions;

import net.imglib2.Interval;
import net.imglib2.atlas.MainFrame;
import net.imglib2.atlas.labeling.Labeling;
import org.scijava.ui.behaviour.util.RunnableAction;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class SetLabelsAction {

	MainFrame.Extensible extensible;

	public SetLabelsAction(MainFrame.Extensible extensible) {
		this.extensible = extensible;
		extensible.addAction(new RunnableAction("Change Available Labels", this::changeLabels), "");
	}

	private void changeLabels() {
		Labeling labeling = extensible.getLabeling();
		List<String> labels = labeling.getLabels();
		Optional<List<String>> results = dialog(labels);
		if(results.isPresent())
			extensible.setLabeling(new Labeling(results.get(), (Interval) labeling));
	}

	private static Optional<List<String>> dialog(List<String> labels) {
		JTextArea textArea = new JTextArea();
		StringJoiner joiner = new StringJoiner("\n");
		labels.forEach(joiner::add);
		textArea.setPreferredSize(new Dimension(200, 200));
		textArea.setText(joiner.toString());
		int result = JOptionPane.showConfirmDialog(null,
				new Object[]{"Please input new labels:", new JScrollPane(textArea)},
				"Change Available Labels",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if(result == JOptionPane.OK_OPTION)
			return Optional.of(Arrays.asList(textArea.getText().split("\n")));
		else
			return Optional.empty();
	}

	public static void main(String... args) {
		Optional<List<String>> result = SetLabelsAction.dialog(Arrays.asList("Hello", "World"));
		System.out.println(result);
	}
}