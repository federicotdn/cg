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
	
	private final ReflectiveMaterial reflectiveMaterial;
	
	//ior channel
	private final double ior;
	private final Texture iorTexture;
	private final Vec2 iorTextureOffset;
	private final Vec2 iorTextureScale;

	public RefractiveMaterial(Channel refractionColorChannel, Channel reflectivityColorChannel, Channel iorChannel) {
        this.reflectiveMaterial = new ReflectiveMaterial(reflectivityColorChannel);
		
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
        Color reflectiveTexColor = reflectiveMaterial.getColor(col.u, col.v);

        //Refractive:
        Color refractiveTexColor = getRefractionColor(col.u, col.v);

        //IOR:
        double iorTex = getIor(col.u, col.v);
		
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

        double r = fresnel(n1, n2, cosI, sen2t);
        if (ray.getHops() <= scene.getRefractionTraceDepth()) {
            if (sen2t <= 1) {
                Vec3 refraction = getSample(n, sen2t, cosI, dir, normal);
				Ray refractionRay = new Ray(col.getPosition().sum(normal.mul(-0.00001)), refraction, Double.POSITIVE_INFINITY, ray.getHops() + 1, !ray.isInsidePrimitive(), true);
                QuickCollision qc = scene.collideRay(refractionRay);
                if (qc != null) {
                    Collision refractionCol = qc.completeCollision();
                    refractedColor = refractionCol.getPrimitive().getMaterial().getSurfaceColor(refractionCol, scene).mul(refractiveTexColor);
                }
            }
        }

        Color reflectedColor = reflectiveMaterial.getSurfaceColor(col, scene);

        return refractedColor.mul(1-r).sum(reflectedColor.mul(r));
    }

	@Override
	public PathData traceSurfaceColor(Collision col, Scene scene) {
        //Get channel values:
        //Reflective:
        Color reflectiveTexColor = reflectiveMaterial.getColor(col.u, col.v);

        //Refractive:
        Color refractiveTexColor = getRefractionColor(col.u, col.v);

        //IOR:
        double iorTex = getIor(col.u, col.v);

        PathData pd = new PathData(Color.BLACK);
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

        double r = fresnel(n1, n2, cosI, sen2t);
        if (ray.getHops() <= scene.getMaxTraceDepth()) {
            if (sen2t <= 1) {
                Vec3 refraction = getSample(n, sen2t, cosI, dir, normal);
                Ray refractionRay = new Ray(col.getPosition().sum(normal.mul(-0.00001)), refraction, Double.POSITIVE_INFINITY, ray.getHops() + 1, !ray.isInsidePrimitive(), true);
                QuickCollision qc = scene.collideRay(refractionRay);
                if (qc != null) {
                    Collision refractionCol = qc.completeCollision();
                    refractedColor = refractionCol.getPrimitive().getMaterial().traceSurfaceColor(refractionCol, scene).color;
                }
            }
        }

        Color reflectedColor = reflectiveMaterial.traceSurfaceColor(col, scene).color;

        pd.color = refractedColor.mul(1-r).sum(reflectedColor.mul(r));
        return pd;
	}

    private Color getRefractionColor(double u, double v) {
        Color refractiveTexColor = refractionColor;
        if (refractionColorTexture != null) {
            Color texCol = refractionColorTexture.getOffsetScaledSample(refractionColorTextureOffset, refractionColorTextureScale, u, v);
            refractiveTexColor = refractiveTexColor.mul(texCol);
        }
        return refractiveTexColor;
    }

    private double getIor(double u, double v) {
        double iorTex = ior;
        if (iorTexture != null) {
            Color texColor = iorTexture.getOffsetScaledSample(iorTextureOffset, iorTextureScale, u, v);
            iorTex *= texColor.getRed(); // Assuming image is grayscale
            iorTex = MathUtils.clamp(iorTex, 1, iorTex);
        }
        return iorTex;
    }

    private double fresnel(double n1, double n2, double cosI, double sen2t) {
        double r;
        if (sen2t > 1) {
            r = 1;
        } else {
            double r0 = Math.pow((n1 - n2)/(n1 + n2), 2);
            double cos;
            if (n1 <= n2) {
                cos = cosI;
            } else {
                cos = Math.sqrt(1 - sen2t);
            }

            r = r0 + ((1 - r0)*(Math.pow(1 - cos, 5)));
        }
        return MathUtils.clamp(r);
    }

    private Vec3 getSample(double n, double sen2t, double cosI, Vec3 dir, Vec3 normal) {
        return dir.mul(n).sub(normal.mul((-n * cosI) + Math.sqrt(1 - sen2t)));
    }
}
