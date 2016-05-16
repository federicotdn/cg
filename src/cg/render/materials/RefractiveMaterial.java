package cg.render.materials;

import cg.math.MathUtils;
import cg.math.Vec2;
import cg.math.Vec3;
import cg.parser.Channel;
import cg.render.*;
import cg.render.assets.Texture;

/**
 * Created by Hobbit on 5/12/16.
 */
public class RefractiveMaterial extends Material {
    public int tir = 0;
    public int rayCount = 0;
    
	//New fields:
	
	//refraction channel
	private final Color refractionColor;
	private final Texture refractionColorTexture;
	private final Vec2 refractionColorTextureOffset;
	private final Vec2 refractionColorTextureScale;
	
	//reflective channel
	private final Color reflectivityColor;
	private final Texture reflectivityColorTexture;
	private final Vec2 reflectivityColorTextureOffset;
	private final Vec2 reflectivityColorTextureScale;
	
	//ior channel
	private final double ior;
	private final Texture iorTexture;
	private final Vec2 iorTextureOffset;
	private final Vec2 iorTextureScale;

	public RefractiveMaterial(Channel refractionColorChannel, Channel reflectivityColorChannel, Channel iorChannel) {
		this.reflectivityColor = reflectivityColorChannel.colorComponent;
		if (reflectivityColorChannel.isTextured()) {
			this.reflectivityColorTexture = reflectivityColorChannel.getTexture();
			this.reflectivityColorTextureOffset = reflectivityColorChannel.textureOffset;
			this.reflectivityColorTextureScale = reflectivityColorChannel.textureScale;
		} else {
			this.reflectivityColorTexture = null;
			this.reflectivityColorTextureOffset = null;
			this.reflectivityColorTextureScale = null;			
		}
		
		this.refractionColor = refractionColorChannel.colorComponent;
		if (refractionColorChannel.isTextured()) {
			this.refractionColorTexture = refractionColorChannel.getTexture();
			this.refractionColorTextureOffset = refractionColorChannel.textureOffset;
			this.refractionColorTextureScale = refractionColorChannel.textureScale;
		} else {
			this.refractionColorTexture = null;
			this.refractionColorTextureOffset = null;
			this.refractionColorTextureScale = null;			
		}
		
		this.ior = iorChannel.scalarComponent;
		if (iorChannel.isTextured()) {
			this.iorTexture = iorChannel.getTexture();
			this.iorTextureOffset = iorChannel.textureOffset;
			this.iorTextureScale = iorChannel.textureScale;
		} else {
			this.iorTexture = null;
			this.iorTextureOffset = null;
			this.iorTextureScale = null;			
		}
	}
	
    @Override
    public Color getSurfaceColor(Collision col, Scene scene) {
    	//Get channel values:
    	//Reflective:
        Color reflectiveTexColor = reflectivityColor;
        if (reflectivityColorTexture != null) {
        	Color texCol = reflectivityColorTexture.getOffsetScaledSample(reflectivityColorTextureOffset, reflectivityColorTextureScale, col.u, col.v);
        	reflectiveTexColor = reflectiveTexColor.mul(texCol);
        }
        
        //Refractive:
        Color refractiveTexColor = refractionColor;
        if (reflectivityColorTexture != null) {
        	Color texCol = refractionColorTexture.getOffsetScaledSample(refractionColorTextureOffset, refractionColorTextureScale, col.u, col.v);
        	refractiveTexColor = refractiveTexColor.mul(texCol);
        }
    	
        //IOR:
		double iorTex = ior;
		if (iorTexture != null) {
			Color texColor = iorTexture.getOffsetScaledSample(iorTextureOffset, iorTextureScale, col.u, col.v);
			double val = (texColor.getRed() + texColor.getBlue() + texColor.getGreen());
			iorTex *= (val / 3); // Assuming image is grayscale
		}
		
        Color refractedColor = Color.BLACK;
        Ray ray = col.getRay();

        Vec3 normal = col.getNormal();
        double n1 = 1;
        double n2 = iorTex;
        if (ray.isInsidePrimitive()) {
            normal = normal.mul(-1);
            n1 = iorTex;
            n2 = 1;
        }

        Vec3 dir = ray.getDirection();

        double n = n1/n2;
        double cosI = - normal.dot(dir);
        double sen2t = (n * n) * (1 - (cosI * cosI));
        Vec3 refraction = dir.mul(n).sub(normal.mul((n * cosI) + Math.sqrt(1 - sen2t)));

        double r;
        if (sen2t > 1) {
            r = 1;
        } else {
            double r0 = Math.pow((n1 - n2)/(n1 + n2), 2);
            double cos;
            if (n1 <= n2) {
                cos = - normal.dot(ray.getDirection());
            } else {
                cos = Math.sqrt(1 - sen2t);
            }

            r = r0 + ((1 - r0)*(Math.pow(1 - cos, 5)));
        }
        r = MathUtils.clamp(r);
        if (ray.getHops() <= scene.getRefractionTraceDepth()) {
            if (sen2t <= 1) {
                Ray refractionRay = new Ray(col.getPosition().sum(normal.mul(-0.005)), refraction, Double.POSITIVE_INFINITY, ray.getHops() + 1, !ray.isInsidePrimitive(), true);
                QuickCollision qc = scene.collideRay(refractionRay);
                if (qc != null) {
                    Collision refractionCol = qc.completeCollision();
                    refractedColor = refractionCol.getPrimitive().getMaterial().getSurfaceColor(refractionCol, scene).mul(refractiveTexColor);
                }
            }
        }

        Color reflectedColor = scene.BACKGROUND_COLOR;
        if (ray.getHops() <= scene.getReflectionTraceDepth()) {
            Vec3 d = col.getRay().getDirection().mul(-1);
            Vec3 reflection = d.reflect(col.getNormal());

            Ray reflectionRay = new Ray(col.getPosition().sum(col.getNormal().mul(0.001)), reflection, Double.POSITIVE_INFINITY, col.getRay().getHops() + 1);
            QuickCollision qc =  scene.collideRay(reflectionRay);
            if (qc != null) {
                Collision reflectionCol = qc.completeCollision();
                Color reflectionColor = reflectionCol.getPrimitive().getMaterial().getSurfaceColor(reflectionCol, scene);
                reflectedColor = reflectiveTexColor.mul(reflectionColor);
            }
        }

        return refractedColor.mul(1-r).sum(reflectedColor.mul(r));
    }
}
