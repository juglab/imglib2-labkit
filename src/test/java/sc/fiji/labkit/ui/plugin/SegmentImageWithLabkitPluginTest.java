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

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imglib2.test.ImgLib2Assert;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.command.CommandService;
import sc.fiji.labkit.ui.utils.TestResources;

public class SegmentImageWithLabkitPluginTest {

	@Test
	public void test() throws Exception {
		// setup
		try (Context context = new Context()) {
			DatasetIOService io = context.service(DatasetIOService.class);
			CommandService cs = context.service(CommandService.class);
			Dataset image = io.open(TestResources.fullPath("/blobs.tif"));
			String blobsModel = TestResources.fullPath("/blobs.classifier");
			Dataset expectedImage = io.open(TestResources.fullPath("/blobs_segmentation.tif"));
			// process
			Dataset output = (Dataset) cs.run(SegmentImageWithLabkitPlugin.class, true,
				"input", image,
				"segmenter_file", blobsModel,
				"use_gpu", false)
				.get().getOutput("output");
			// test
			ImgLib2Assert.assertImageEqualsRealType(expectedImage, output, 0.0);
		}
	}
}
