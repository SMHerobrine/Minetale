package com.smherobrine.minetale.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.smherobrine.minetale.Minetale;
import com.smherobrine.minetale.block.ForgottenTempleGatewayBlock;
import com.smherobrine.minetale.block.entity.ForgottenTempleGatewayBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class ForgottenTempleGatewayRenderer implements BlockEntityRenderer<ForgottenTempleGatewayBlockEntity, ForgottenTempleGatewayRenderer.State> {
	private static final Identifier PORTAL_EDGE = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "textures/entity/forgotten_temple_portal_edge.png");
	private static final Identifier PORTAL_INNER = Identifier.fromNamespaceAndPath(Minetale.MOD_ID, "textures/entity/forgotten_temple_portal_inner.png");
	private static final float RING_SIZE = 3.2F;
	private static final float CORE_SIZE = 2.55F;
	private static final float GLOW_SIZE = 4.05F;
	private static final float DEPTH_STEP = 0.0045F;

	public ForgottenTempleGatewayRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(ForgottenTempleGatewayBlockEntity blockEntity, State renderState, float partialTick, Vec3 cameraPos,
		ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPos, crumblingOverlay);

		BlockState blockState = blockEntity.getBlockState();
		renderState.shouldRender = ForgottenTempleGatewayBlock.isCenter(blockState) && blockState.getValue(ForgottenTempleGatewayBlock.ACTIVE);
		renderState.animationTime = blockEntity.getLevel() == null ? 0.0F : blockEntity.getLevel().getGameTime() + partialTick;
	}

	@Override
	public void submit(State renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
		if (!renderState.shouldRender) {
			return;
		}

		float pulse = 0.94F + (float) Math.sin(renderState.animationTime * 0.12F) * 0.08F;
		float shimmer = 0.88F + (float) Math.cos(renderState.animationTime * 0.19F) * 0.12F;
		float hover = 2.0F + (float) Math.sin(renderState.animationTime * 0.08F) * 0.12F;

		poseStack.pushPose();
		poseStack.translate(0.5D, hover, 0.5D);
		poseStack.mulPose(cameraRenderState.orientation);
		poseStack.mulPose(Axis.XP.rotationDegrees((float) Math.sin(renderState.animationTime * 0.05F) * 3.0F));

		submitLayer(
			poseStack,
			submitNodeCollector,
			PORTAL_EDGE,
			renderState.lightCoords,
			GLOW_SIZE,
			GLOW_SIZE,
			renderState.animationTime * 0.65F,
			-DEPTH_STEP * 3.0F,
			70,
			true
		);
		submitLayer(
			poseStack,
			submitNodeCollector,
			PORTAL_EDGE,
			renderState.lightCoords,
			RING_SIZE,
			RING_SIZE,
			renderState.animationTime * 1.1F,
			-DEPTH_STEP * 2.0F,
			250,
			true
		);
		submitLayer(
			poseStack,
			submitNodeCollector,
			PORTAL_EDGE,
			renderState.lightCoords,
			RING_SIZE * 0.93F,
			RING_SIZE * 0.93F,
			-renderState.animationTime * 0.85F,
			-DEPTH_STEP,
			155,
			false
		);

		submitLayer(
			poseStack,
			submitNodeCollector,
			PORTAL_INNER,
			renderState.lightCoords,
			CORE_SIZE * pulse,
			CORE_SIZE * pulse,
			-renderState.animationTime * 1.75F,
			0.0F,
			220,
			false
		);
		submitLayer(
			poseStack,
			submitNodeCollector,
			PORTAL_INNER,
			renderState.lightCoords,
			CORE_SIZE * 0.9F,
			CORE_SIZE * 0.9F * shimmer,
			renderState.animationTime * 2.6F,
			DEPTH_STEP,
			175,
			false
		);
		submitLayer(
			poseStack,
			submitNodeCollector,
			PORTAL_INNER,
			renderState.lightCoords,
			CORE_SIZE * 0.66F,
			CORE_SIZE * 0.66F,
			-renderState.animationTime * 4.2F,
			DEPTH_STEP * 2.0F,
			165,
			true
		);
		submitLayer(
			poseStack,
			submitNodeCollector,
			PORTAL_INNER,
			renderState.lightCoords,
			CORE_SIZE * 0.36F,
			CORE_SIZE * 0.36F,
			renderState.animationTime * 7.0F,
			DEPTH_STEP * 3.0F,
			140,
			true
		);

		submitParticleArc(poseStack, submitNodeCollector, renderState.lightCoords, renderState.animationTime, 7, 1.52F, 1.52F, DEPTH_STEP * 4.5F);
		submitParticleArc(poseStack, submitNodeCollector, renderState.lightCoords, -renderState.animationTime * 0.85F, 5, 0.96F, 0.96F, DEPTH_STEP * 5.5F);

		poseStack.popPose();
	}

	private static void submitLayer(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Identifier texture, int packedLight, float size,
		float rotationDegrees, int alpha) {
		submitLayer(poseStack, submitNodeCollector, texture, packedLight, size, size, rotationDegrees, 0.0F, alpha, false);
	}

	private static void submitLayer(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Identifier texture, int packedLight, float size,
		float rotationDegrees, int alpha, boolean emissive) {
		submitLayer(poseStack, submitNodeCollector, texture, packedLight, size, size, rotationDegrees, 0.0F, alpha, emissive);
	}

	private static void submitLayer(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Identifier texture, int packedLight, float width, float height,
		float rotationDegrees, float depth, int alpha, boolean emissive) {
		poseStack.pushPose();
		poseStack.mulPose(Axis.ZP.rotationDegrees(rotationDegrees));
		poseStack.translate(0.0F, 0.0F, depth);
		float halfWidth = width * 0.5F;
		float halfHeight = height * 0.5F;

		submitNodeCollector.submitCustomGeometry(
			poseStack,
			emissive ? RenderTypes.entityTranslucentEmissive(texture) : RenderTypes.entityTranslucent(texture),
			(pose, vertexConsumer) -> {
				addVertex(vertexConsumer, pose, -halfWidth, -halfHeight, 0.0F, 0.0F, 1.0F, packedLight, alpha);
				addVertex(vertexConsumer, pose, halfWidth, -halfHeight, 0.0F, 1.0F, 1.0F, packedLight, alpha);
				addVertex(vertexConsumer, pose, halfWidth, halfHeight, 0.0F, 1.0F, 0.0F, packedLight, alpha);
				addVertex(vertexConsumer, pose, -halfWidth, halfHeight, 0.0F, 0.0F, 0.0F, packedLight, alpha);
			}
		);

		poseStack.popPose();
	}

	private static void submitParticleArc(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, float time, int count, float widthRadius,
		float heightRadius, float depth) {
		for (int i = 0; i < count; i++) {
			float progress = (float) i / (float) count;
			float angle = time * 0.075F + progress * ((float) Math.PI * 2.0F);
			float x = (float) Math.cos(angle) * widthRadius;
			float y = (float) Math.sin(angle * 1.65F) * heightRadius * 0.42F;
			float size = 0.045F + (0.03F * (0.5F + 0.5F * (float) Math.sin(time * 0.16F + i)));
			int alpha = 110 + (int) (90.0F * (0.5F + 0.5F * Math.sin(time * 0.21F + i * 1.7F)));

			poseStack.pushPose();
			poseStack.translate(x, y, depth + i * 0.0006F);
			poseStack.mulPose(Axis.ZP.rotationDegrees(time * 3.5F + i * 41.0F));
			submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(PORTAL_INNER), (pose, vertexConsumer) -> {
				addVertex(vertexConsumer, pose, -size, -size, 0.0F, 0.0F, 1.0F, packedLight, alpha);
				addVertex(vertexConsumer, pose, size, -size, 0.0F, 1.0F, 1.0F, packedLight, alpha);
				addVertex(vertexConsumer, pose, size, size, 0.0F, 1.0F, 0.0F, packedLight, alpha);
				addVertex(vertexConsumer, pose, -size, size, 0.0F, 0.0F, 0.0F, packedLight, alpha);
			});
			poseStack.popPose();
		}
	}

	private static void addVertex(VertexConsumer vertexConsumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int packedLight,
		int alpha) {
		Matrix4f matrix4f = pose.pose();
		vertexConsumer.addVertex(matrix4f, x, y, z)
			.setColor(255, 255, 255, alpha)
			.setUv(u, v)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(packedLight)
			.setNormal(pose, 0.0F, 0.0F, 1.0F);
	}

	public static final class State extends BlockEntityRenderState {
		private boolean shouldRender;
		private float animationTime;
	}
}
