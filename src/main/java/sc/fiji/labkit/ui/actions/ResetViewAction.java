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

package sc.fiji.labkit.ui.actions;

import org.scijava.plugin.Plugin;
import org.scijava.ui.behaviour.io.gui.CommandDescriptionProvider;
import org.scijava.ui.behaviour.io.gui.CommandDescriptions;
import org.scijava.ui.behaviour.util.Actions;

import sc.fiji.labkit.ui.Extensible;
import sc.fiji.labkit.ui.LabKitKeymapManager;
import sc.fiji.labkit.ui.MenuBar;
import sc.fiji.labkit.ui.models.ImageLabelingModel;
import sc.fiji.labkit.ui.models.TransformationModel;

/**
 * Implements the reset view menu item.
 *
 * @author Matthias Arzt
 */
public class ResetViewAction {


	public ResetViewAction(Extensible extensible, ImageLabelingModel model) {
		this(null, extensible, model);
	}
	
	public ResetViewAction(Actions actions, Extensible extensible, ImageLabelingModel model) {
		Runnable action = () -> {
			TransformationModel transformationModel = model.transformationModel();
			transformationModel.transformToShowInterval(model.labeling().get()
				.interval(), model.labelTransformation());
		};
		extensible.addMenuItem(MenuBar.VIEW_MENU, "Reset View", 100,
			ignore -> action.run(), null, "");

		if (actions != null) {
			actions.runnableAction(() -> action.run(), RESET_VIEW_ACTION, RESET_VIEW_KEYS);
		}
	}

	private static final String RESET_VIEW_ACTION = "reset view";
	private static final String[] RESET_VIEW_KEYS = new String[] { "not mapped" };
	private static final String RESET_VIEW_DESCRIPTION = "Reset the current image position, zoom and rotation.";

	@Plugin(type = CommandDescriptionProvider.class)
	public static class Descriptions extends CommandDescriptionProvider {
		public Descriptions() {
			super(LabKitKeymapManager.LABKIT_SCOPE, LabKitKeymapManager.LABKIT_CONTEXT);
		}

		@Override
		public void getCommandDescriptions(final CommandDescriptions descriptions) {
			descriptions.add(RESET_VIEW_ACTION, RESET_VIEW_KEYS, RESET_VIEW_DESCRIPTION);
		}
	}
}
