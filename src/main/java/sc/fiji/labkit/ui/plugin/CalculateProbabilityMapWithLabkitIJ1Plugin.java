/*-
 * #%L
 * The Labkit image segmentation tool for Fiji.
 * %%
 * Copyright (C) 2017 - 2024 Matthias Arzt
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

package sc.fiji.labkit.ui.plugin;

import ij.ImagePlus;
import net.imglib2.img.VirtualStackAdapter;
import net.imglib2.img.display.imagej.ImageJFunctions;
import org.scijava.Cancelable;
import org.scijava.Context;
import org.scijava.ItemIO;
import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import sc.fiji.labkit.ui.segmentation.SegmentationTool;
import sc.fiji.labkit.ui.utils.progress.StatusServiceProgressWriter;

import java.io.File;

/**
 * @author Robert Haase
 * @author Matthias Arzt
 */
@Plugin(type = Command.class,
	menuPath = "Plugins > Labkit > Macro Recordable > Calculate Probability Map With Labkit (IJ1)")
public class CalculateProbabilityMapWithLabkitIJ1Plugin implements Command, Cancelable {

	@Parameter
	private Context context;

	@Parameter
	private StatusService statusService;

	@Parameter
	private ImagePlus input;

	@Parameter
	private File segmenter_file;

	@Parameter(type = ItemIO.OUTPUT)
	private ImagePlus output;

	@Parameter(required = false)
	private Boolean use_gpu = false;

	@Override
	public void run() {
		SegmentationTool segmenter = new SegmentationTool(context);
		segmenter.openModel(segmenter_file.getAbsolutePath());
		segmenter.setUseGpu(use_gpu);
		segmenter.setProgressWriter(new StatusServiceProgressWriter(statusService));
		output = ImageJFunctions.wrap(
			segmenter.probabilityMap(VirtualStackAdapter.wrap(input)), "");
		output.show();
	}

	@Override
	public boolean isCanceled() {
		return false;
	}

	@Override
	public void cancel(String reason) {

	}

	@Override
	public String getCancelReason() {
		return null;
	}
}
