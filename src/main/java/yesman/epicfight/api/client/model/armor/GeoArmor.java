package yesman.epicfight.api.client.model.armor;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.util.RenderUtils;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.Mesh;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SingleVertex;
import yesman.epicfight.api.utils.math.Vec2f;
import yesman.epicfight.api.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class GeoArmor extends ArmorModelTransformer {
	static final PartTransformer<GeoCube> HEAD = new SimpleBaker("head", 9);
	static final PartTransformer<GeoCube> LEFT_FEET = new SimpleBaker("leftBoots", 5);
	static final PartTransformer<GeoCube> RIGHT_FEET = new SimpleBaker("rightBoots", 2);
	static final PartTransformer<GeoCube> LEFT_ARM = new Limb("leftArm", 16, 17, 19, 1.1875F, false);
	static final PartTransformer<GeoCube> LEFT_ARM_CHILD = new SimpleSeparateBaker("leftArm", 16, 17, 1.1875F);
	static final PartTransformer<GeoCube> RIGHT_ARM = new Limb("rightArm", 11, 12, 14, 1.1875F, false);
	static final PartTransformer<GeoCube> RIGHT_ARM_CHILD = new SimpleSeparateBaker("rightArm", 11, 12, 1.1875F);
	static final PartTransformer<GeoCube> LEFT_LEG = new Limb("leftLeg", 4, 5, 6, 0.375F, true);
	static final PartTransformer<GeoCube> LEFT_LEG_CHILD = new SimpleSeparateBaker("leftLeg", 4, 5, 0.375F);
	static final PartTransformer<GeoCube> RIGHT_LEG = new Limb("rightLeg", 1, 2, 3, 0.375F, true);
	static final PartTransformer<GeoCube> RIGHT_LEG_CHILD = new SimpleSeparateBaker("rightLeg", 1, 2, 0.375F);
	static final PartTransformer<GeoCube> CHEST = new Chest("chest");
	static final PartTransformer<GeoCube> CHEST_CHILD = new SimpleSeparateBaker("chest", 8, 7, 1.125F);
	
	static int indexCount = 0;
	
	@OnlyIn(Dist.CLIENT)
	static class GeoModelPartition {
		final PartTransformer<GeoCube> partTransformer;
		final PartTransformer<GeoCube> partChildTransformer;
		final IBone geoBone;
		
		private GeoModelPartition(PartTransformer<GeoCube> partTransformer, PartTransformer<GeoCube> partChildTransformer, IBone geoBone) {
			this.partTransformer = partTransformer;
			this.partChildTransformer = partChildTransformer;
			this.geoBone = geoBone;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AnimatedMesh transformModel(HumanoidModel<?> model, ArmorItem armorItem, EquipmentSlot slot, boolean debuggingMode) {
		if (!(armorItem instanceof IAnimatable) || !(model instanceof GeoArmorRenderer<?>)) {
			return null;
		}
		
		GeoArmorRenderer geoModel = (GeoArmorRenderer)model;
		List<GeoModelPartition> boxes = Lists.newArrayList();
		
		IBone headBone = geoModel.getGeoModelProvider().getBone(geoModel.headBone);
		IBone bodyBone = geoModel.getGeoModelProvider().getBone(geoModel.bodyBone);
		IBone rightArmBone = geoModel.getGeoModelProvider().getBone(geoModel.rightArmBone);
		IBone leftArmBone = geoModel.getGeoModelProvider().getBone(geoModel.leftArmBone);
		IBone rightLegBone = geoModel.getGeoModelProvider().getBone(geoModel.rightLegBone);
		IBone leftLegBone = geoModel.getGeoModelProvider().getBone(geoModel.leftLegBone);
		IBone rightBootBone = geoModel.getGeoModelProvider().getBone(geoModel.rightBootBone);
		IBone leftBootBone = geoModel.getGeoModelProvider().getBone(geoModel.leftBootBone);
		
		headBone.setRotationX(0);
		headBone.setRotationY(0);
		headBone.setRotationZ(0);
		
		bodyBone.setRotationX(0);
		bodyBone.setRotationY(0);
		bodyBone.setRotationZ(0);
		
		rightArmBone.setRotationX(0);
		rightArmBone.setRotationY(0);
		rightArmBone.setRotationZ(0);
		
		leftArmBone.setRotationX(0);
		leftArmBone.setRotationY(0);
		leftArmBone.setRotationZ(0);
		
		rightLegBone.setRotationX(0);
		rightLegBone.setRotationY(0);
		rightLegBone.setRotationZ(0);
		
		leftLegBone.setRotationX(0);
		leftLegBone.setRotationY(0);
		leftLegBone.setRotationZ(0);
		
		rightBootBone.setRotationX(0);
		rightBootBone.setRotationY(0);
		rightBootBone.setRotationZ(0);
		
		leftBootBone.setRotationX(0);
		leftBootBone.setRotationY(0);
		leftBootBone.setRotationZ(0);
		
		switch (slot) {
		case HEAD:
			boxes.add(new GeoModelPartition(HEAD, HEAD, headBone));
			break;
		case CHEST:
			boxes.add(new GeoModelPartition(CHEST, CHEST_CHILD, bodyBone));
			boxes.add(new GeoModelPartition(RIGHT_ARM, RIGHT_ARM_CHILD, rightArmBone));
			boxes.add(new GeoModelPartition(LEFT_ARM, LEFT_ARM_CHILD, leftArmBone));
			break;
		case LEGS:
			boxes.add(new GeoModelPartition(CHEST, CHEST_CHILD, bodyBone));
			boxes.add(new GeoModelPartition(LEFT_LEG, LEFT_LEG_CHILD, leftLegBone));
			boxes.add(new GeoModelPartition(RIGHT_LEG, RIGHT_LEG_CHILD, rightLegBone));
			break;
		case FEET:
			boxes.add(new GeoModelPartition(LEFT_FEET, LEFT_FEET, leftBootBone));
			boxes.add(new GeoModelPartition(RIGHT_FEET, RIGHT_FEET, rightBootBone));
			break;
		default:
			return null;
		}
		
		Mesh.RenderProperties.Builder propertyBuilder = Mesh.RenderProperties.builder();
		propertyBuilder.customTexturePath(geoModel.getTextureLocation((ArmorItem & IAnimatable)armorItem).toString());
		
		ResourceLocation rl = new ResourceLocation(ForgeRegistries.ITEMS.getKey(armorItem).getNamespace(), "armor/" + ForgeRegistries.ITEMS.getKey(armorItem).getPath());
		AnimatedMesh armorModelMesh = bakeMeshFromCubes(boxes, propertyBuilder, debuggingMode);
		Meshes.addMesh(rl, armorModelMesh);
		
		return armorModelMesh;
	}
	
	private static AnimatedMesh bakeMeshFromCubes(List<GeoModelPartition> partitions, Mesh.RenderProperties.Builder propertyBuilder, boolean debuggingMode) {
		List<SingleVertex> vertices = Lists.newArrayList();
		Map<String, List<Integer>> indices = Maps.newHashMap();
		PoseStack poseStack = new PoseStack();
		indexCount = 0;
		
		for (GeoModelPartition modelpartition : partitions) {
			if (modelpartition.geoBone instanceof GeoBone geoBone) {
				bake(poseStack, modelpartition, geoBone, modelpartition.partTransformer, vertices, indices, debuggingMode);
			}
		}
		
		return SingleVertex.loadVertexInformationWithRenderProperty(vertices, indices, propertyBuilder);
	}
	
	private static void bake(PoseStack poseStack, GeoModelPartition modelpartition, GeoBone geoBone, PartTransformer<GeoCube> partBaker, List<SingleVertex> vertices, Map<String, List<Integer>> indices, boolean debuggingMode) {
		if (geoBone == null) {
			return;
		}
		
		poseStack.pushPose();
		
		if (!geoBone.isHidden()) {
			RenderUtils.prepMatrixForBone(poseStack, geoBone);
			
			for (GeoCube cube : geoBone.childCubes) {
				poseStack.pushPose();
				
				RenderUtils.translateToPivotPoint(poseStack, cube);
				RenderUtils.rotateMatrixAroundCube(poseStack, cube);
				RenderUtils.translateAwayFromPivotPoint(poseStack, cube);
				
				partBaker.bakeCube(poseStack, cube, vertices, indices);
				poseStack.popPose();
			}
		}
		
		if (!geoBone.childBonesAreHiddenToo()) {
			for (GeoBone childParts : geoBone.childBones) {
				bake(poseStack, modelpartition, childParts, modelpartition.partChildTransformer, vertices, indices, debuggingMode);
			}
		}
		
		poseStack.popPose();
	}
	
	@OnlyIn(Dist.CLIENT)
	static class SimpleBaker extends PartTransformer<GeoCube> {
		final int jointId;
		
		public SimpleBaker(String partName, int jointId) {
			super(partName);
			this.jointId = jointId;
		}
		
		public void bakeCube(PoseStack poseStack, GeoCube cube, List<SingleVertex> vertices, Map<String, List<Integer>> indices) {
			for (GeoQuad quad : cube.quads) {
				if (quad == null) {
					continue;
				}
				
				Vector3f norm = quad.normal.copy();
				norm.transform(poseStack.last().normal());
				
				for (GeoVertex vertex : quad.vertices) {
					Vector4f pos = new Vector4f(vertex.position);
					pos.transform(poseStack.last().pose());
					
					vertices.add(new SingleVertex()
						.setPosition(new Vec3f(pos.x(), pos.y(), pos.z())/*.scale(0.0625F)*/)
						.setNormal(new Vec3f(norm.x(), norm.y(), norm.z()))
						.setTextureCoordinate(new Vec2f(vertex.textureU, vertex.textureV))
						.setEffectiveJointIDs(new Vec3f(this.jointId, 0, 0))
						.setEffectiveJointWeights(new Vec3f(1.0F, 0.0F, 0.0F))
						.setEffectiveJointNumber(1)
					);
				}
				
				putIndexCount(indices, indexCount);
				putIndexCount(indices, indexCount + 1);
				putIndexCount(indices, indexCount + 3);
				putIndexCount(indices, indexCount + 3);
				putIndexCount(indices, indexCount + 1);
				putIndexCount(indices, indexCount + 2);
				indexCount+=4;
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	static class SimpleSeparateBaker extends PartTransformer<GeoCube> {
		final SimpleBaker upperBaker;
		final SimpleBaker lowerBaker;
		final float yClipCoord;
		
		public SimpleSeparateBaker(String partName, int upperJoint, int lowerJoint, float yClipCoord) {
			super(partName);
			this.upperBaker = new SimpleBaker(partName + "Upper", upperJoint);
			this.lowerBaker = new SimpleBaker(partName + "Lower", lowerJoint);
			this.yClipCoord = yClipCoord;
		}
		
		@Override
		public void bakeCube(PoseStack poseStack, GeoCube cube, List<SingleVertex> vertices, Map<String, List<Integer>> indices) {
			Vector4f cubeCenter = new Vector4f((float)cube.pivot.x(), (float)cube.pivot.y(), (float)cube.pivot.z(), 1.0F);
			cubeCenter.transform(poseStack.last().pose());
			
			if (cubeCenter.y() > this.yClipCoord) {
				this.upperBaker.bakeCube(poseStack, cube, vertices, indices);
			} else {
				this.lowerBaker.bakeCube(poseStack, cube, vertices, indices);
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	static class Chest extends PartTransformer<GeoCube> {
		static final float X_PLANE = 0.0F;
		static final VertexWeight[] WEIGHT_ALONG_Y = { new VertexWeight(13.6666F, 0.230F, 0.770F), new VertexWeight(15.8333F, 0.254F, 0.746F), new VertexWeight(18.0F, 0.5F, 0.5F), new VertexWeight(20.1666F, 0.744F, 0.256F), new VertexWeight(22.3333F, 0.770F, 0.230F)};
		
		public Chest(String partName) {
			super(partName);
		}
		
		@Override
		public void bakeCube(PoseStack poseStack, GeoCube cube, List<SingleVertex> vertices, Map<String, List<Integer>> indices) {
			List<AnimatedPolygon> xClipPolygons = Lists.newArrayList();
			List<AnimatedPolygon> xyClipPolygons = Lists.newArrayList();
			
			for (GeoQuad polygon : cube.quads) {
				Matrix4f matrix = poseStack.last().pose();
				
				ModelPart.Vertex pos0 = getTranslatedVertex(polygon.vertices[0], matrix);
				ModelPart.Vertex pos1 = getTranslatedVertex(polygon.vertices[1], matrix);
				ModelPart.Vertex pos2 = getTranslatedVertex(polygon.vertices[2], matrix);
				ModelPart.Vertex pos3 = getTranslatedVertex(polygon.vertices[3], matrix);
				Direction direction = getDirectionFromVector(polygon.normal);
				
				VertexWeight pos0Weight = getYClipWeight(pos0.pos.y());
				VertexWeight pos1Weight = getYClipWeight(pos1.pos.y());
				VertexWeight pos2Weight = getYClipWeight(pos2.pos.y());
				VertexWeight pos3Weight = getYClipWeight(pos3.pos.y());
				
				if (pos1.pos.x() > X_PLANE != pos2.pos.x() > X_PLANE) {
					float distance = pos2.pos.x() - pos1.pos.x();
					float textureU = pos1.u + (pos2.u - pos1.u) * ((X_PLANE - pos1.pos.x()) / distance);
					ModelPart.Vertex pos4 = new ModelPart.Vertex(X_PLANE, pos0.pos.y(), pos0.pos.z(), textureU, pos0.v);
					ModelPart.Vertex pos5 = new ModelPart.Vertex(X_PLANE, pos1.pos.y(), pos1.pos.z(), textureU, pos1.v);
					
					xClipPolygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(pos0, 8, 7, 0, pos0Weight.chestWeight, pos0Weight.torsoWeight, 0),
						new AnimatedVertex(pos4, 8, 7, 0, pos0Weight.chestWeight, pos0Weight.torsoWeight, 0),
						new AnimatedVertex(pos5, 8, 7, 0, pos1Weight.chestWeight, pos1Weight.torsoWeight, 0),
						new AnimatedVertex(pos3, 8, 7, 0, pos3Weight.chestWeight, pos3Weight.torsoWeight, 0)
					}, direction));
					xClipPolygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(pos4, 8, 7, 0, pos0Weight.chestWeight, pos0Weight.torsoWeight, 0),
						new AnimatedVertex(pos1, 8, 7, 0, pos1Weight.chestWeight, pos1Weight.torsoWeight, 0),
						new AnimatedVertex(pos2, 8, 7, 0, pos2Weight.chestWeight, pos2Weight.torsoWeight, 0),
						new AnimatedVertex(pos5, 8, 7, 0, pos1Weight.chestWeight, pos1Weight.torsoWeight, 0)
					}, direction));
				} else {
					xClipPolygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(pos0, 8, 7, 0, pos0Weight.chestWeight, pos0Weight.torsoWeight, 0),
						new AnimatedVertex(pos1, 8, 7, 0, pos1Weight.chestWeight, pos1Weight.torsoWeight, 0),
						new AnimatedVertex(pos2, 8, 7, 0, pos2Weight.chestWeight, pos2Weight.torsoWeight, 0),
						new AnimatedVertex(pos3, 8, 7, 0, pos3Weight.chestWeight, pos3Weight.torsoWeight, 0)
					}, direction));
				}
			}
			
			for (AnimatedPolygon polygon : xClipPolygons) {
				boolean upsideDown = polygon.animatedVertexPositions[1].pos.y() > polygon.animatedVertexPositions[2].pos.y();
				AnimatedVertex pos0 = upsideDown ? polygon.animatedVertexPositions[2] : polygon.animatedVertexPositions[0];
				AnimatedVertex pos1 = upsideDown ? polygon.animatedVertexPositions[3] : polygon.animatedVertexPositions[1];
				AnimatedVertex pos2 = upsideDown ? polygon.animatedVertexPositions[0] : polygon.animatedVertexPositions[2];
				AnimatedVertex pos3 = upsideDown ? polygon.animatedVertexPositions[1] : polygon.animatedVertexPositions[3];
				Direction direction = getDirectionFromVector(polygon.normal);
				List<VertexWeight> vertexWeights = getMiddleYClipWeights(pos1.pos.y(), pos2.pos.y());
				List<AnimatedVertex> animatedVertices = Lists.newArrayList();
				animatedVertices.add(pos0);
				animatedVertices.add(pos1);
				
				if (vertexWeights.size() > 0) {
					for (VertexWeight vertexWeight : vertexWeights) {
						float distance = pos2.pos.y() - pos1.pos.y();
						float textureV = pos1.v + (pos2.v - pos1.v) * ((vertexWeight.yClipCoord - pos1.pos.y()) / distance);
						ModelPart.Vertex pos4 = new ModelPart.Vertex(pos0.pos.x(), vertexWeight.yClipCoord, pos0.pos.z(), pos0.u, textureV);
						ModelPart.Vertex pos5 = new ModelPart.Vertex(pos1.pos.x(), vertexWeight.yClipCoord, pos1.pos.z(), pos1.u, textureV);
						animatedVertices.add(new AnimatedVertex(pos4, 8, 7, 0, vertexWeight.chestWeight, vertexWeight.torsoWeight, 0));
						animatedVertices.add(new AnimatedVertex(pos5, 8, 7, 0, vertexWeight.chestWeight, vertexWeight.torsoWeight, 0));
					}
				}
				
				animatedVertices.add(pos3);
				animatedVertices.add(pos2);
				
				for (int i = 0; i < (animatedVertices.size() - 2) / 2; i++) {
					int start = i*2;
					AnimatedVertex p0 = animatedVertices.get(start);
					AnimatedVertex p1 = animatedVertices.get(start + 1);
					AnimatedVertex p2 = animatedVertices.get(start + 3);
					AnimatedVertex p3 = animatedVertices.get(start + 2);
					xyClipPolygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(p0, 8, 7, 0, p0.weight.x, p0.weight.y, 0),
						new AnimatedVertex(p1, 8, 7, 0, p1.weight.x, p1.weight.y, 0),
						new AnimatedVertex(p2, 8, 7, 0, p2.weight.x, p2.weight.y, 0),
						new AnimatedVertex(p3, 8, 7, 0, p3.weight.x, p3.weight.y, 0)
					}, direction));
				}
			}
			
			for (AnimatedPolygon polygon : xyClipPolygons) {
				Vector3f norm = polygon.normal.copy();
				norm.transform(poseStack.last().normal());
				
				for (AnimatedVertex vertex : polygon.animatedVertexPositions) {
					Vector4f pos = new Vector4f(vertex.pos);
					float weight1 = vertex.weight.x;
					float weight2 = vertex.weight.y;
					int joint1 = vertex.jointId.getX();
					int joint2 = vertex.jointId.getY();
					int count = weight1 > 0.0F && weight2 > 0.0F ? 2 : 1;
					
					if (weight1 <= 0.0F) {
						joint1 = joint2;
						weight1 = weight2;
					}
					
					vertices.add(new SingleVertex()
						.setPosition(new Vec3f(pos.x(), pos.y(), pos.z()))
						.setNormal(new Vec3f(norm.x(), norm.y(), norm.z()))
						.setTextureCoordinate(new Vec2f(vertex.u, vertex.v))
						.setEffectiveJointIDs(new Vec3f(joint1, joint2, 0))
						.setEffectiveJointWeights(new Vec3f(weight1, weight2, 0.0F))
						.setEffectiveJointNumber(count)
					);
				}
				
				putIndexCount(indices, indexCount);
				putIndexCount(indices, indexCount + 1);
				putIndexCount(indices, indexCount + 3);
				putIndexCount(indices, indexCount + 3);
				putIndexCount(indices, indexCount + 1);
				putIndexCount(indices, indexCount + 2);
				indexCount+=4;
			}
		}
		
		static VertexWeight getYClipWeight(float y) {
			if (y < WEIGHT_ALONG_Y[0].yClipCoord) {
				return new VertexWeight(y, 0.0F, 1.0F);
			}
			
			int index = -1;
			for (int i = 0; i < WEIGHT_ALONG_Y.length; i++) {
				
			}
			
			if (index > 0) {
				VertexWeight pair = WEIGHT_ALONG_Y[index];
				return new VertexWeight(y, pair.chestWeight, pair.torsoWeight);
			}
			
			return new VertexWeight(y, 1.0F, 0.0F);
		}
		
		static List<VertexWeight> getMiddleYClipWeights(float minY, float maxY) {
			List<VertexWeight> cutYs = Lists.newArrayList();
			for (VertexWeight vertexWeight : WEIGHT_ALONG_Y) {
				if (vertexWeight.yClipCoord > minY && maxY >= vertexWeight.yClipCoord) {
					cutYs.add(vertexWeight);
				}
			}
			return cutYs;
		}
		
		static class VertexWeight {
			final float yClipCoord;
			final float chestWeight;
			final float torsoWeight;
			
			public VertexWeight(float yClipCoord, float chestWeight, float torsoWeight) {
				this.yClipCoord = yClipCoord;
				this.chestWeight = chestWeight;
				this.torsoWeight = torsoWeight;
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	static class Limb extends PartTransformer<GeoCube> {
		final int upperJoint;
		final int lowerJoint;
		final int middleJoint;
		final float yClipCoord;
		final boolean bendInFront;
		
		public Limb(String partName, int upperJoint, int lowerJoint, int middleJoint, float yClipCoord, boolean bendInFront) {
			super(partName);
			this.upperJoint = upperJoint;
			this.lowerJoint = lowerJoint;
			this.middleJoint = middleJoint;
			this.yClipCoord = yClipCoord;
			this.bendInFront = bendInFront;
		}
		
		@Override
		public void bakeCube(PoseStack poseStack, GeoCube cube, List<SingleVertex> vertices, Map<String, List<Integer>> indices) {
			List<AnimatedPolygon> polygons = Lists.newArrayList();
			
			for (GeoQuad quad : cube.quads) {
				Matrix4f matrix = poseStack.last().pose();
				ModelPart.Vertex pos0 = getTranslatedVertex(quad.vertices[0], matrix);
				ModelPart.Vertex pos1 = getTranslatedVertex(quad.vertices[1], matrix);
				ModelPart.Vertex pos2 = getTranslatedVertex(quad.vertices[2], matrix);
				ModelPart.Vertex pos3 = getTranslatedVertex(quad.vertices[3], matrix);
				Direction direction = getDirectionFromVector(quad.normal);
				
				if (pos1.pos.y() > this.yClipCoord != pos2.pos.y() > this.yClipCoord) {
					float distance = pos2.pos.y() - pos1.pos.y();
					float textureV = pos1.v + (pos2.v - pos1.v) * ((this.yClipCoord - pos1.pos.y()) / distance);
					ModelPart.Vertex pos4 = new ModelPart.Vertex(pos0.pos.x(), this.yClipCoord, pos0.pos.z(), pos0.u, textureV);
					ModelPart.Vertex pos5 = new ModelPart.Vertex(pos1.pos.x(), this.yClipCoord, pos1.pos.z(), pos1.u, textureV);
					
					int upperId, lowerId;
					if (distance > 0) {
						upperId = this.lowerJoint;
						lowerId = this.upperJoint;
					} else {
						upperId = this.upperJoint;
						lowerId = this.lowerJoint;
					}
					
					polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(pos0, upperId), new AnimatedVertex(pos1, upperId),
						new AnimatedVertex(pos5, upperId), new AnimatedVertex(pos4, upperId)
					}, direction));
					polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(pos4, lowerId), new AnimatedVertex(pos5, lowerId),
						new AnimatedVertex(pos2, lowerId), new AnimatedVertex(pos3, lowerId)
					}, direction));
					
					boolean hasSameZ = pos4.pos.z() < 0.0F == pos5.pos.z() < 0.0F;
					boolean isFront = hasSameZ && (pos4.pos.z() < 0.0F == this.bendInFront);
					
					if (isFront) {
						polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
							new AnimatedVertex(pos4, this.middleJoint), new AnimatedVertex(pos5, this.middleJoint),
							new AnimatedVertex(pos5, this.upperJoint), new AnimatedVertex(pos4, this.upperJoint)
						}, 0.001F, direction));
						polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
							new AnimatedVertex(pos4, this.lowerJoint), new AnimatedVertex(pos5, this.lowerJoint),
							new AnimatedVertex(pos5, this.middleJoint), new AnimatedVertex(pos4, this.middleJoint)
						}, 0.001F, direction));
					} else if (!hasSameZ) {
						boolean startFront = pos4.pos.z() > 0;
						int firstJoint = this.lowerJoint;
						int secondJoint = this.lowerJoint;
						int thirdJoint = startFront ? this.upperJoint : this.middleJoint;
						int fourthJoint = startFront ? this.middleJoint : this.upperJoint;
						int fifthJoint = this.upperJoint;
						int sixthJoint = this.upperJoint;
						
						polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
							new AnimatedVertex(pos4, firstJoint), new AnimatedVertex(pos5, secondJoint),
							new AnimatedVertex(pos5, thirdJoint), new AnimatedVertex(pos4, fourthJoint)
						}, 0.001F, direction));
						polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
							new AnimatedVertex(pos4, fourthJoint), new AnimatedVertex(pos5, thirdJoint),
							new AnimatedVertex(pos5, fifthJoint), new AnimatedVertex(pos4, sixthJoint)
						}, 0.001F, direction));
					}
				} else {
					int jointId = pos0.pos.y() > this.yClipCoord ? this.upperJoint : this.lowerJoint;
					polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(pos0, jointId), new AnimatedVertex(pos1, jointId),
						new AnimatedVertex(pos2, jointId), new AnimatedVertex(pos3, jointId)
					}, direction));
				}
			}
			
			for (AnimatedPolygon quad : polygons) {
				Vector3f norm = quad.normal.copy();
				norm.transform(poseStack.last().normal());
				
				for (AnimatedVertex vertex : quad.animatedVertexPositions) {
					Vector4f pos = new Vector4f(vertex.pos);
					
					vertices.add(new SingleVertex()
						.setPosition(new Vec3f(pos.x(), pos.y(), pos.z()))
						.setNormal(new Vec3f(norm.x(), norm.y(), norm.z()))
						.setTextureCoordinate(new Vec2f(vertex.u, vertex.v))
						.setEffectiveJointIDs(new Vec3f(vertex.jointId.getX(), 0, 0))
						.setEffectiveJointWeights(new Vec3f(1.0F, 0.0F, 0.0F))
						.setEffectiveJointNumber(1)
					);
				}
				
				putIndexCount(indices, indexCount);
				putIndexCount(indices, indexCount + 1);
				putIndexCount(indices, indexCount + 3);
				putIndexCount(indices, indexCount + 3);
				putIndexCount(indices, indexCount + 1);
				putIndexCount(indices, indexCount + 2);
				indexCount+=4;
			}
		}
	}
	
	static Direction getDirectionFromVector(Vector3f directionVec) {
		for (Direction direction : Direction.values()) {
			Vector3f direcVec = new Vector3f(Float.compare(directionVec.x(), -0.0F) == 0 ? 0.0F : directionVec.x(), directionVec.y(), directionVec.z());
			if (direcVec.equals(direction.step())) {
				return direction;
			}
		}
		
		return null;
	}
	
	static ModelPart.Vertex getTranslatedVertex(GeoVertex original, Matrix4f matrix) {
		Vector4f translatedPosition = new Vector4f(original.position);
		translatedPosition.transform(matrix);
		
		return new ModelPart.Vertex(translatedPosition.x(), translatedPosition.y(), translatedPosition.z(), original.textureU, original.textureV);
	}
	
	@OnlyIn(Dist.CLIENT)
	static class AnimatedVertex extends ModelPart.Vertex {
		final Vec3i jointId;
		final Vec3f weight;
		
		public AnimatedVertex(ModelPart.Vertex posTexVertx, int jointId) {
			this(posTexVertx, jointId, 0, 0, 1.0F, 0.0F, 0.0F);
		}
		
		public AnimatedVertex(ModelPart.Vertex posTexVertx, int jointId1, int jointId2, int jointId3, float weight1, float weight2, float weight3) {
			this(posTexVertx, new Vec3i(jointId1, jointId2, jointId3), new Vec3f(weight1, weight2, weight3));
		}
		
		public AnimatedVertex(ModelPart.Vertex posTexVertx, Vec3i ids, Vec3f weights) {
			this(posTexVertx, posTexVertx.u, posTexVertx.v, ids, weights);
		}
		
		public AnimatedVertex(ModelPart.Vertex posTexVertx, float u, float v, Vec3i ids, Vec3f weights) {
			super(posTexVertx.pos.x(), posTexVertx.pos.y(), posTexVertx.pos.z(), u, v);
			this.jointId = ids;
			this.weight = weights;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	static class AnimatedPolygon {
		public final AnimatedVertex[] animatedVertexPositions;
		public final Vector3f normal;
		
		public AnimatedPolygon(AnimatedVertex[] positionsIn, Direction directionIn) {
			this.animatedVertexPositions = positionsIn;
			this.normal = directionIn.step();
		}
		
		public AnimatedPolygon(AnimatedVertex[] positionsIn, float cor, Direction directionIn) {
			this.animatedVertexPositions = positionsIn;
			positionsIn[0] = new AnimatedVertex(positionsIn[0], positionsIn[0].u, positionsIn[0].v + cor, positionsIn[0].jointId, positionsIn[0].weight);
			positionsIn[1] = new AnimatedVertex(positionsIn[1], positionsIn[1].u, positionsIn[1].v + cor, positionsIn[1].jointId, positionsIn[1].weight);
			positionsIn[2] = new AnimatedVertex(positionsIn[2], positionsIn[2].u, positionsIn[2].v - cor, positionsIn[2].jointId, positionsIn[2].weight);
			positionsIn[3] = new AnimatedVertex(positionsIn[3], positionsIn[3].u, positionsIn[3].v - cor, positionsIn[3].jointId, positionsIn[3].weight);
			this.normal = directionIn.step();
		}
	}
}