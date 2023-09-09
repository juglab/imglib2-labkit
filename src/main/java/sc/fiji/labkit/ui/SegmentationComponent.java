/*-
 * #%L
 * The Labkit image segmentation tool for Fiji.
 * %%
 * Copyright (C) 2017 - 2023 Matthias Arzt
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package sc.fiji.labkit.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.scijava.ui.behaviour.util.Actions;
import org.scijava.ui.behaviour.util.InputActionBindings;

import bdv.ui.keymap.Keymap;
import bdv.ui.keymap.Keymap.UpdateListener;
import bdv.ui.keymap.KeymapManager;
import net.miginfocom.swing.MigLayout;
import sc.fiji.labkit.ui.actions.AddLabelingIoAction;
import sc.fiji.labkit.ui.actions.BatchSegmentAction;
import sc.fiji.labkit.ui.actions.BitmapImportExportAction;
import sc.fiji.labkit.ui.actions.ClassifierIoAction;
import sc.fiji.labkit.ui.actions.ClassifierSettingsAction;
import sc.fiji.labkit.ui.actions.ExampleAction;
import sc.fiji.labkit.ui.actions.LabelEditAction;
import sc.fiji.labkit.ui.actions.LabelingIoAction;
import sc.fiji.labkit.ui.actions.ResetViewAction;
import sc.fiji.labkit.ui.actions.SegmentationAsLabelAction;
import sc.fiji.labkit.ui.actions.SegmentationExportAction;
import sc.fiji.labkit.ui.actions.ShowHelpAction;
import sc.fiji.labkit.ui.actions.ShowPreferencesDialogAction;
import sc.fiji.labkit.ui.menu.MenuKey;
import sc.fiji.labkit.ui.models.ColoredLabelsModel;
import sc.fiji.labkit.ui.models.Holder;
import sc.fiji.labkit.ui.models.ImageLabelingModel;
import sc.fiji.labkit.ui.models.SegmentationItem;
import sc.fiji.labkit.ui.models.SegmentationModel;
import sc.fiji.labkit.ui.panel.ImageInfoPanel;
import sc.fiji.labkit.ui.panel.LabelPanel;
import sc.fiji.labkit.ui.panel.SegmenterPanel;
import sc.fiji.labkit.ui.plugin.MeasureConnectedComponents;
import sc.fiji.labkit.ui.segmentation.PredictionLayer;
import sc.fiji.labkit.ui.segmentation.TrainClassifier;

/**
 * {@link SegmentationComponent} is the central Labkit UI component. Provides UI
 * to display and modify a {@link SegmentationModel}.
 * <p>
 * The UI consist of a Big Data Viewer panel, with brush tools and a side bar.
 * The side bar lists panels and segmentation algorithms.
 * <p>
 * A main menu that contains many actions for open and saving data, can be
 * accessed by using {@link #getMenuBar()}.
 */
public class SegmentationComponent extends JPanel implements AutoCloseable {

	private final boolean unmodifiableLabels;

	private final DefaultExtensible extensible;

	private final BasicLabelingComponent labelingComponent;

	private final SegmentationModel segmentationModel;

	private final KeymapManager keymapManager;

	private final Actions actions;

	private final InputActionBindings keybindings;

	private final JFrame dialogBoxOwner;


	public SegmentationComponent(JFrame dialogBoxOwner,
		SegmentationModel segmentationModel, boolean unmodifiableLabels)
	{
		this.dialogBoxOwner = dialogBoxOwner;
		this.extensible = new DefaultExtensible(segmentationModel.context(),
			dialogBoxOwner);
		this.unmodifiableLabels = unmodifiableLabels;
		this.segmentationModel = segmentationModel;

		keybindings = new InputActionBindings();
		SwingUtilities.replaceUIActionMap( this, keybindings.getConcatenatedActionMap() );
		SwingUtilities.replaceUIInputMap( this, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, keybindings.getConcatenatedInputMap() );
		keymapManager = new LabKitKeymapManager();
		final Keymap keymap = keymapManager.getForwardSelectedKeymap();
		actions = new Actions( keymap.getConfig(), new String[] { LabKitKeymapManager.LABKIT_CONTEXT } );
		actions.install( keybindings, "labkit" );
		final UpdateListener updateListener = () -> actions.updateKeyConfig( keymap.getConfig() );
		keymap.updateListeners().add( updateListener );

		ImageLabelingModel imageLabelingModel = segmentationModel.imageLabelingModel();
		labelingComponent = new BasicLabelingComponent(dialogBoxOwner, imageLabelingModel, keymapManager);
		labelingComponent.addBdvLayer(PredictionLayer.createPredictionLayer(segmentationModel));

		initActions();
		setLayout(new BorderLayout());
		add(initGui());
	}

	private void initActions() {
		final Holder<SegmentationItem> selectedSegmenter = segmentationModel
			.segmenterList().selectedSegmenter();
		final ImageLabelingModel labelingModel = segmentationModel
			.imageLabelingModel();
		new TrainClassifier(extensible, segmentationModel.segmenterList());
		new ClassifierSettingsAction(extensible, segmentationModel.segmenterList());
		new ClassifierIoAction(extensible, segmentationModel.segmenterList());
		new LabelingIoAction(extensible, labelingModel);
		new AddLabelingIoAction(extensible, labelingModel.labeling());
		new SegmentationExportAction(extensible, labelingModel);
		new ResetViewAction(extensible, labelingModel);
		new BatchSegmentAction(extensible, selectedSegmenter);
		new SegmentationAsLabelAction(extensible, segmentationModel);
		new BitmapImportExportAction(extensible, labelingModel);
		new LabelEditAction(extensible, unmodifiableLabels, new ColoredLabelsModel(
			labelingModel));
		MeasureConnectedComponents.addAction(extensible, labelingModel);
		new ShowHelpAction(extensible);
		new ShowPreferencesDialogAction( extensible, actions, keymapManager, dialogBoxOwner );
		new ExampleAction( actions );
		labelingComponent.addShortcuts(extensible.getShortCuts());
	}

	private JPanel initLeftPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("", "[grow]", "[][grow][grow]"));
		panel.add(ImageInfoPanel.newFramedImageInfoPanel(segmentationModel.imageLabelingModel(),
			labelingComponent), "grow, wrap");
		panel.add(LabelPanel.newFramedLabelPanel(segmentationModel
			.imageLabelingModel(), extensible, unmodifiableLabels), "grow, wrap, height 0:50");
		panel.add(SegmenterPanel.newFramedSegmeterPanel(segmentationModel.segmenterList(),
			extensible), "grow, height 0:50");
		panel.invalidate();
		panel.repaint();
		return panel;
	}

	private JSplitPane initGui() {
		JSplitPane panel = new JSplitPane();
		panel.setOneTouchExpandable(true);
		panel.setLeftComponent(initLeftPanel());
		panel.setRightComponent(labelingComponent);
		panel.setBorder(BorderFactory.createEmptyBorder());
		return panel;
	}

	@Deprecated
	public JComponent getComponent() {
		return this;
	}

	public JMenu createMenu(MenuKey<Void> key) {
		if (key == MenuBar.SEGMENTER_MENU)
			return extensible.createMenu(
				SegmentationItem.SEGMENTER_MENU, segmentationModel
					.segmenterList().selectedSegmenter()::get);
		return extensible.createMenu(key, () -> null);
	}

	@Override
	public void close() {
		labelingComponent.close();
	}

	public JMenuBar getMenuBar() {
		return new MenuBar(this::createMenu);
	}

	public void autoContrast() {
		labelingComponent.autoContrast();
	}
}
