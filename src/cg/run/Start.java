package cg.run;

import cg.parser.SceneParser;
import cg.render.Image;
import cg.render.Scene;
import cg.test.SpheresScene;
import org.apache.commons.cli.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Start {

	private static final int DEFAULT_SAMPLES = 100;
	
	private static class RenderInfo {
		public String inputPath;
		public String outputPath;
		public int runs;
		public boolean includeTime;
		public boolean usePathTracing;
		public int pathTracingSamples;
		public boolean test;
	}
	
	private static RenderInfo parseRenderInfo(String[] args) {
		Options options = new Options();
		options.addOption("i", true, "JSON input file path");
		options.addOption("o", true, "PNG image output file path");
		options.addOption("time", false, "Include render information on image");
		options.addOption("benchmark", true, "Enable benchmarking (n runs)");
		options.addOption("pathtracer", false, "Enable path tracing");
		options.addOption("s", true, "Path tracing samples");
		options.addOption("test", false, "Enable test mode");
		
		CommandLineParser cmdParser = new DefaultParser();
		CommandLine cmd;
		try {
			cmd = cmdParser.parse(options, args);
		} catch (ParseException e) {
			return null;
		}
		
		String output = cmd.getOptionValue("o");
		
		String input = cmd.getOptionValue("i");
		if (input == null) {
			return null;
		}
		
		if (output == null) {
			String prefix = input.substring(0, input.lastIndexOf('.'));
			output = prefix + ".png";
		}
		
		int runs = 1;
		String runsStr = cmd.getOptionValue("benchmark");
		if (runsStr != null) {
			try {
				runs = Integer.parseInt(runsStr);
			} catch (NumberFormatException e) {
				return null;
			}
			
			if (runs < 1) {
				return null;
			}
		}
		
		RenderInfo ri = new RenderInfo();
		
		ri.includeTime = cmd.hasOption("time");
		ri.test = cmd.hasOption("test");
		ri.inputPath = input;
		ri.outputPath = output;
		ri.runs = runs;
		
		ri.usePathTracing = cmd.hasOption("pathtracer");
		if (ri.usePathTracing) {
			ri.pathTracingSamples = DEFAULT_SAMPLES;
			String samplesStr = cmd.getOptionValue("s");
			if (samplesStr != null) {
				try {
					ri.pathTracingSamples = Integer.parseInt(samplesStr);
				} catch (NumberFormatException e) {
					return null;
				}
			}
			
			if (ri.pathTracingSamples < 1) {
				return null;
			}
		}
		
		return ri;
	}
	
	public static void main(String[] args) {			
		RenderInfo ri = parseRenderInfo(args);
		if (ri == null) {
			System.out.println("Invalid command line parameters, exiting.");
			System.exit(1);
		}
		
		System.out.println("Parsed arguments:");
		System.out.println("Input file path: " + ri.inputPath);
		System.out.println("Ouput file path: " + ri.outputPath);
		System.out.println("Show render info: " + ri.includeTime);
		System.out.println("Render runs: " + ri.runs);
		System.out.println("Path tracing enabled: " + ri.usePathTracing);
		if (ri.usePathTracing) {
			System.out.println("Path tracing samples: " + ri.pathTracingSamples);
		}
		System.out.println();
		
		Scene scene;
		long parseTime;
		
		if (ri.test) {
			System.out.println("Test mode enabled.");
			System.out.println();
			parseTime = 0;
			scene = SpheresScene.fillScene();
		} else {
			System.out.println("Loading scene...");
			SceneParser parser = new SceneParser(ri.inputPath);
			System.out.println("Scene loaded.  Parsing scene...");
			
			long parseStartTime = System.currentTimeMillis();
			scene = parser.parseScene(ri.usePathTracing, ri.pathTracingSamples);
			parseTime = System.currentTimeMillis() - parseStartTime;
			
			if (scene == null) {
				System.out.println("Scene was not parsed correctly, exiting.");
				System.exit(1);
			}
			
			System.out.println("Scene parsed. (time: " + getPrettyTime(parseTime) + ")");
			System.out.println();			
		}
		
		if (ri.usePathTracing) {
			System.out.println("+++ Path tracing is enabled. +++");
			System.out.println();
		}
		
		System.out.println("Settings:");

		if (!ri.test) {			
			printSettings(scene, ri);
		}
		
		System.out.println();
		
		Image img = null;

		System.out.println("Begin runs. (count: " + ri.runs + ")");
		System.out.println();

		long totalRunTimes = 0;
		
		for (int i = 0; i < ri.runs; i++) {
			System.out.println("Begin run #" + (i + 1) + "...");
			long startRunTime = System.currentTimeMillis();
			img = scene.render();
			long runTime = System.currentTimeMillis() - startRunTime;
			totalRunTimes += runTime;
			
			String runRenderTime = getPrettyTime(runTime);

			if (ri.includeTime) {
				System.out.println("Run ended. Render time: " + runRenderTime);
			} else {
				System.out.println("Run ended.");
			}
			System.out.println();
		}
		

		long avgTime = totalRunTimes / ri.runs;
		String renderTime = getPrettyTime(avgTime);
		
		System.out.println("=======");
		System.out.println("Average render time: " + renderTime);
		System.out.println("=======");
		System.out.println();

		img.enableGammaCorrection();
		
		if (ri.includeTime) {
			System.out.println("Adding render info to image.");
			System.out.println();

			String imageInfo = "Render time: ";
			imageInfo += renderTime + " (" + img.getWidth() + "x" + img.getHeight() + "). Threads: "
					+ scene.getThreads() + ". Bucket size: " + scene.getBucketSize() + ". ";

			if (scene.isPathTracingEnabled()) {
				imageInfo += "Samples: " + scene.getPathTracingSamples() + ". Trace depth: " + scene.getMaxTraceDepth() + " . Mode: Path tracing.";
			} else {
				imageInfo += "Samples: " + scene.getSamples() + ". Reflection TD: " + scene.getReflectionTraceDepth()
						+ ". Refraction TD: " + scene.getRefractionTraceDepth() + ".Mode: Ray tracing.";
			}
			
			Graphics graphics = img.getBufferedImage().getGraphics();
			graphics.setColor(new Color(0, 0, 0, 0.5f));
			graphics.fillRect(0, img.getHeight() - 40, img.getWidth(), 40);
			graphics.setColor(Color.WHITE);
			graphics.setFont(graphics.getFont().deriveFont(24));
			graphics.drawString(imageInfo, 15, img.getHeight() - 15);			
		}

		System.out.println("Writing image file" + (img.isGammaCorrect() ? "(gamma correction enabled)" : "") + ".");
		try {
			img.writeFile(ri.outputPath);
		} catch (Exception e) {
			System.out.println("Error writing image file.  Exiting.");
			System.exit(1);
		}
		System.out.println("All done.");
		System.out.println("Total time: " + getPrettyTime(totalRunTimes + parseTime));
	}

	private static String getPrettyTime(long t) {
		return TimeUnit.MILLISECONDS.toMinutes(t) + "m " +
				TimeUnit.MILLISECONDS.toSeconds(t) % 60 + "s " + t % 1000 + "ms";
	}
	
	private static void printSettings(Scene scene, RenderInfo settings) {
		System.out.println("Image size: " + scene.getWidth() + "x" + scene.getHeight() + ".");
		System.out.println("Mode: " + (scene.isPathTracingEnabled() ? "Path tracing" : "Ray tracing"));

		System.out.println("Using " + scene.getThreads() + " threads.");
		System.out.println("Bucket size: " + scene.getBucketSize() + ".");

		if (!scene.isPathTracingEnabled()) {
			if (scene.getSamples() == 1) {
				System.out.println("Antialiasing is disabled.");
			} else {
				System.out.println("Antialiasing set to " + scene.getSamples() + " samples.");
			}
			System.out.println("Refraction trace depth: " + scene.getRefractionTraceDepth() + ".");
			System.out.println("Reflection trace depth: " + scene.getReflectionTraceDepth() + ".");
		} else {
			System.out.println("Ray trace depth: " + scene.getMaxTraceDepth());
			System.out.println("Samples: " + scene.getPathTracingSamples());
		}
	}
}
