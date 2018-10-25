/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.cyberagent.android.gpuimage.util;

import java.util.LinkedList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;

public class GPUImageFilterTools {
	public static void showDialog(final Context context, final OnGpuImageFilterChosenListener listener) {
		final FilterList filters = new FilterList();
		filters.addFilter("Sepia", FilterType.SEPIA);
		filters.addFilter("Grayscale", FilterType.GRAYSCALE);
		filters.addFilter("Emboss", FilterType.EMBOSS);
		filters.addFilter("Vignette", FilterType.VIGNETTE);
		filters.addFilter("Gaussian Blur", FilterType.GAUSSIAN_BLUR);
		filters.addFilter("Haze", FilterType.HAZE);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Choose a filter");
		builder.setItems(filters.names.toArray(new String[filters.names.size()]), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int item) {
				listener.onGpuImageFilterChosenListener(createFilterForType(context, filters.filters.get(item)));
			}
		});
		builder.create().show();
	}

	public static GPUImageFilter createFilterForType(final Context context, final FilterType type) {
		switch (type) {
		case GRAYSCALE:
			return new GPUImageGrayscaleFilter();
		case SEPIA:
			return new GPUImageSepiaFilter();
		case EMBOSS:
			return new GPUImageEmbossFilter();
		case VIGNETTE:
			PointF centerPoint = new PointF();
			centerPoint.x = 0.5f;
			centerPoint.y = 0.5f;
			return new GPUImageVignetteFilter(centerPoint, new float[] { 0.0f, 0.0f, 0.0f }, 0.3f, 0.75f);

		case GAUSSIAN_BLUR:
			return new GPUImageGaussianBlurFilter();

		case HAZE:
			return new GPUImageHazeFilter();

		default:
			throw new IllegalStateException("No filter of that type!");
		}

	}

	public interface OnGpuImageFilterChosenListener {
		void onGpuImageFilterChosenListener(GPUImageFilter filter);
	}

	public enum FilterType {
		NORMAL,SEPIA, GRAYSCALE, EMBOSS, VIGNETTE, GAUSSIAN_BLUR, HAZE
	}

	private static class FilterList {
		public List<String> names = new LinkedList<String>();
		public List<FilterType> filters = new LinkedList<FilterType>();

		public void addFilter(final String name, final FilterType filter) {
			names.add(name);
			filters.add(filter);
		}
	}

	public static class FilterAdjuster {
		private final Adjuster<? extends GPUImageFilter> adjuster;

		public FilterAdjuster(final GPUImageFilter filter) {
			if (filter instanceof GPUImageSepiaFilter) {
				adjuster = new SepiaAdjuster().filter(filter);
			} else if (filter instanceof GPUImageEmbossFilter) {
				adjuster = new EmbossAdjuster().filter(filter);
			} else if (filter instanceof GPUImageVignetteFilter) {
				adjuster = new VignetteAdjuster().filter(filter);
			} else if (filter instanceof GPUImageGaussianBlurFilter) {
				adjuster = new GaussianBlurAdjuster().filter(filter);
			} else if (filter instanceof GPUImageHazeFilter) {
				adjuster = new HazeAdjuster().filter(filter);
			} else {
				adjuster = null;
			}
		}

		public boolean canAdjust() {
			return adjuster != null;
		}

		public void adjust(final int percentage) {
			if (adjuster != null) {
				adjuster.adjust(percentage);
			}
		}

		private abstract class Adjuster<T extends GPUImageFilter> {
			private T filter;

			@SuppressWarnings("unchecked")
			public Adjuster<T> filter(final GPUImageFilter filter) {
				this.filter = (T) filter;
				return this;
			}

			public T getFilter() {
				return filter;
			}

			public abstract void adjust(int percentage);

			protected float range(final int percentage, final float start, final float end) {
				return (end - start) * percentage / 100.0f + start;
			}

			protected int range(final int percentage, final int start, final int end) {
				return (end - start) * percentage / 100 + start;
			}
		}

		private class SepiaAdjuster extends Adjuster<GPUImageSepiaFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setIntensity(range(percentage, 0.0f, 2.0f));
			}
		}

		private class EmbossAdjuster extends Adjuster<GPUImageEmbossFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setIntensity(range(percentage, 0.0f, 4.0f));
			}
		}

		private class VignetteAdjuster extends Adjuster<GPUImageVignetteFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setVignetteStart(range(percentage, 0.0f, 1.0f));
			}
		}

		private class GaussianBlurAdjuster extends Adjuster<GPUImageGaussianBlurFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setBlurSize(range(percentage, 0.0f, 1.0f));
			}
		}

		private class HazeAdjuster extends Adjuster<GPUImageHazeFilter> {
			@Override
			public void adjust(final int percentage) {
				getFilter().setDistance(range(percentage, -0.3f, 0.3f));
				getFilter().setSlope(range(percentage, -0.3f, 0.3f));
			}
		}

	}
}
