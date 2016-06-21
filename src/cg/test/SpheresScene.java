package cg.test;

import java.util.ArrayList;
import java.util.List;

import cg.math.MathUtils;
import cg.math.Vec3;
import cg.rand.MultiJitteredSampler;
import cg.render.Camera;
import cg.render.Color;
import cg.render.Light;
import cg.render.Primitive;
import cg.render.Scene;
import cg.render.lights.PointLight;
import cg.render.materials.Diffuse;
import cg.render.shapes.Sphere;

public class SpheresScene {
	public static Scene fillScene() {
		Scene s = new Scene();
		
		s.setSize(1280, 720);
		s.setBucketSize(32);
		s.setReflectionTraceDepth(4);
		s.setRefractionTraceDepth(4);
		s.setThreads(0);
		s.setMaxTraceDepth(5);
		s.setSamples(1);
		
		int samples = 1024;
		
		MultiJitteredSampler sampler = new MultiJitteredSampler(samples);
		sampler.generateSamples();
		
		Camera cam = new Camera(new Vec3(0,0,-2), new Vec3(), 60);
		s.setCam(cam);
		
		List<Primitive> p = new ArrayList<>();
		
		PointLight point = new PointLight(s, Color.WHITE, 1, new Vec3(0, 0, 0));
		point.calculateTransform();
		s.addLight(point);
		
		for (int i = 0; i < samples; i++) {
			double x = sampler.xCoords[i];
			double y = sampler.yCoords[i];
			Vec3 hemisphereSample = MathUtils.squareToHemisphere(x, y, 0);//.normalize();
			
			Sphere sphere = new Sphere(hemisphereSample, new Vec3(), new Vec3(1, 1, 1), 0.01);
			sphere.setMaterial(Diffuse.DEFAULT_DIFFUSE);
			p.add(sphere);
		}
		
		
		for (Primitive prim : p) {
			prim.calculateTransform();
			s.addPrimitive(prim);
		}
		
		return s;
	}
}
