package com.smherobrine.minetale.client.screen;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.smherobrine.minetale.client.orbis.ClientMemoryState;
import com.smherobrine.minetale.client.orbis.OrbisClientNetworking;
import com.smherobrine.minetale.menu.HeartOfOrbisMenu;
import com.smherobrine.minetale.orbis.OrbisMemoryHelper;
import com.smherobrine.minetale.orbis.OrbisMemoryRewards;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HeartOfOrbisScreen extends AbstractContainerScreen<HeartOfOrbisMenu> {
	private static final int SCREEN_WIDTH = 432;
	private static final int SCREEN_HEIGHT = 312;
	private static final int HEADER_HEIGHT = 24;
	private static final int FOOTER_HEIGHT = 34;
	private static final int LIST_LEFT = 0;
	private static final int LIST_TOP = 32;
	private static final int LIST_WIDTH = 420;
	private static final int LIST_HEIGHT = 230;
	private static final int SCROLLBAR_WIDTH = 8;
	private static final int CARD_COLUMNS = 3;
	private static final int CARD_GAP = 4;
	private static final int CARD_WIDTH = 128;
	private static final int CARD_HEIGHT = 82;
	private static final int MODEL_BOX_SIZE = 52;
	private static final int MODEL_BOX_PADDING = 3;
	private static final int TEXT_LEFT_PADDING = 10;
	private static final int TEXT_RIGHT_PADDING = 8;
	private static final int LIST_CONTENT_LEFT = 8;
	private static final int LIST_CONTENT_TOP = 8;
	private static final int CLOSE_BUTTON_SIZE = 12;
	private static final int CLOSE_BUTTON_RIGHT = 10;
	private static final int CLOSE_BUTTON_TOP = 10;
	private static final int ACTION_BUTTON_WIDTH = 114;
	private static final int ACTION_BUTTON_HEIGHT = 20;
	private static final int ACTION_BUTTON_RIGHT = 14;
	private static final int ACTION_BUTTON_Y = SCREEN_HEIGHT - 32;

	private final List<MobEntry> mobEntries = new ArrayList<>();
	private boolean mobsLoaded;
	private int selectedIndex;
	private double scrollOffset;
	private boolean draggingScrollbar;
	private double scrollbarDragOffset;
	private boolean actionButtonHovered;

	public HeartOfOrbisScreen(HeartOfOrbisMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title, SCREEN_WIDTH, SCREEN_HEIGHT);
		this.inventoryLabelY = 10000;
	}

	@Override
	public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		ensureMobEntries();

		graphics.pose().pushMatrix();
		graphics.pose().translate(this.leftPos, this.topPos);

		renderFrame(graphics);
		renderHeader(graphics, mouseX, mouseY);
		renderMobGrid(graphics, mouseX, mouseY);
		renderFooter(graphics);
		renderTooltips(graphics, mouseX, mouseY);
		updateCursor(graphics, mouseX, mouseY);

		graphics.pose().popMatrix();
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		ensureMobEntries();

		if (isMouseOverCloseButton(event.x(), event.y())) {
			playUiClick(0.95F);
			this.onClose();
			return true;
		}

		if (isMouseOverScrollbar(event.x(), event.y())) {
			this.draggingScrollbar = true;
			this.scrollbarDragOffset = event.y() - getScrollbarThumbYScreen();
			updateScrollFromMouse(event.y());
			return true;
		}

		if (isMouseOverActionButton(event.x(), event.y()) && canClaimSelectedMemory()) {
			MobEntry selected = getSelectedEntry();
			if (selected != null) {
				playUiClick(0.9F);
				OrbisClientNetworking.claimMemory(selected.id().toString());
				return true;
			}
		}

		for (int index = 0; index < this.mobEntries.size(); index++) {
			if (isMouseOverCard(index, event.x(), event.y())) {
				playUiClick(index == this.selectedIndex ? 0.92F : 1.05F);
				this.selectedIndex = index;
				return true;
			}
		}

		return super.mouseClicked(event, doubleClick);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
		if (this.draggingScrollbar) {
			double thumbCenterY = event.y() - this.scrollbarDragOffset + getScrollbarThumbHeight() / 2.0D;
			updateScrollFromMouse(thumbCenterY);
			return true;
		}

		return super.mouseDragged(event, dx, dy);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (this.draggingScrollbar) {
			this.draggingScrollbar = false;
			return true;
		}

		return super.mouseReleased(event);
	}

	@Override
	public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
		if (isMouseInsideList(x, y)) {
			this.scrollOffset = clampScroll(this.scrollOffset - scrollY * 18.0D);
			return true;
		}

		return super.mouseScrolled(x, y, scrollX, scrollY);
	}

	private void ensureMobEntries() {
		if (this.mobsLoaded || this.minecraft.level == null) {
			return;
		}

		BuiltInRegistries.ENTITY_TYPE.stream()
			.filter(OrbisMemoryHelper::isTrackable)
			.map(this::createMobEntry)
			.filter(entry -> entry != null)
			.sorted(Comparator.comparing(entry -> entry.name().getString()))
			.forEach(this.mobEntries::add);

		this.mobsLoaded = true;
		this.selectedIndex = this.mobEntries.isEmpty() ? -1 : 0;
		this.scrollOffset = 0.0D;
	}

	private MobEntry createMobEntry(EntityType<?> type) {
		if (this.minecraft.level == null) {
			return null;
		}

		try {
			Object created = type.create(this.minecraft.level, EntitySpawnReason.COMMAND);
			if (!(created instanceof Mob mob)) {
				return null;
			}

			Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
			return new MobEntry(type, mob, mob.getType().getDescription(), id);
		} catch (Exception ignored) {
			return null;
		}
	}

	private void renderFrame(GuiGraphicsExtractor graphics) {
		graphics.fill(0, 0, this.imageWidth, this.imageHeight, 0xEE0E1725);
		graphics.outline(0, 0, this.imageWidth, this.imageHeight, 0xFF344768);
		graphics.outline(2, 2, this.imageWidth - 4, this.imageHeight - 4, 0xFF1A263B);

		graphics.fill(4, 4, this.imageWidth - 4, 4 + HEADER_HEIGHT, 0xFF223A5B);
		graphics.outline(4, 4, this.imageWidth - 8, HEADER_HEIGHT, 0xFF496488);

		graphics.fill(LIST_LEFT, LIST_TOP, LIST_LEFT + LIST_WIDTH, LIST_TOP + LIST_HEIGHT, 0xAA132033);
		graphics.outline(LIST_LEFT, LIST_TOP, LIST_WIDTH, LIST_HEIGHT, 0xFF203049);

		int footerTop = this.imageHeight - FOOTER_HEIGHT - 6;
		graphics.fill(6, footerTop, this.imageWidth - 6, this.imageHeight - 6, 0xCC101927);
		graphics.outline(6, footerTop, this.imageWidth - 12, FOOTER_HEIGHT, 0xFF223654);
	}

	private void renderHeader(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		String titleText = "MEMORIES " + getCollectedCount() + "/" + getMemoryTotal();
		graphics.centeredText(this.font, Component.literal(titleText), this.imageWidth / 2, 11, 0xFFD7E1F2);

		int closeX = getCloseButtonXLocal();
		int closeY = CLOSE_BUTTON_TOP;
		boolean hovered = isMouseOverCloseButton(mouseX, mouseY);
		int background = hovered ? 0xFF8A4250 : 0xFF5A2D39;
		int border = hovered ? 0xFFE3A0AD : 0xFFB57080;
		graphics.fill(closeX, closeY, closeX + CLOSE_BUTTON_SIZE, closeY + CLOSE_BUTTON_SIZE, background);
		graphics.outline(closeX, closeY, CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE, border);
		graphics.centeredText(this.font, Component.literal("X"), closeX + CLOSE_BUTTON_SIZE / 2, closeY + 2, 0xFFF8EAF0);
	}

	private void renderMobGrid(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		int viewportLeft = LIST_LEFT + 1;
		int viewportTop = LIST_TOP + 1;
		int viewportRight = LIST_LEFT + LIST_WIDTH - SCROLLBAR_WIDTH - 3;
		int viewportBottom = LIST_TOP + LIST_HEIGHT - 1;

		graphics.enableScissor(viewportLeft, viewportTop, viewportRight, viewportBottom);

		int visibleWidth = viewportRight - viewportLeft;
		int contentTop = LIST_TOP + LIST_CONTENT_TOP - (int)this.scrollOffset;

		for (int index = 0; index < this.mobEntries.size(); index++) {
			MobEntry entry = this.mobEntries.get(index);
			int column = index % CARD_COLUMNS;
			int row = index / CARD_COLUMNS;
			int x = LIST_LEFT + LIST_CONTENT_LEFT + column * (CARD_WIDTH + CARD_GAP);
			int y = contentTop + row * (CARD_HEIGHT + CARD_GAP);

			if (y + CARD_HEIGHT < LIST_TOP || y > LIST_TOP + LIST_HEIGHT) {
				continue;
			}

			boolean hovered = isMouseOverCard(index, mouseX, mouseY);
			boolean selected = index == this.selectedIndex;
			int background = selected ? 0xFF31476C : hovered ? 0xFF27374F : 0xFF1F2B40;
			int border = selected ? 0xFF7D8EFF : hovered ? 0xFF60789F : 0xFF30435F;
			int trim = selected ? 0xFF7385E4 : 0xFF455575;
			if (isClaimed(entry)) {
				background = selected ? 0xFF35543D : hovered ? 0xFF30483A : 0xFF24372B;
				border = selected ? 0xFF97DA89 : hovered ? 0xFF73B17A : 0xFF4E7A58;
				trim = 0xFF86D48B;
			} else if (isUnlocked(entry)) {
				background = selected ? 0xFF5A4A27 : hovered ? 0xFF4B3D22 : 0xFF3A301C;
				border = selected ? 0xFFF0CE71 : hovered ? 0xFFD9B660 : 0xFF9E8447;
				trim = 0xFFE4C362;
			}

			graphics.fill(x, y, x + CARD_WIDTH, y + CARD_HEIGHT, background);
			graphics.outline(x, y, CARD_WIDTH, CARD_HEIGHT, border);
			graphics.fill(x, y + CARD_HEIGHT - 4, x + CARD_WIDTH, y + CARD_HEIGHT, trim);

			int boxX = x + 8;
			int boxY = y + (CARD_HEIGHT - MODEL_BOX_SIZE) / 2 - 1;
			graphics.fill(boxX, boxY, boxX + MODEL_BOX_SIZE, boxY + MODEL_BOX_SIZE, 0xCC101827);
			graphics.outline(boxX, boxY, MODEL_BOX_SIZE, MODEL_BOX_SIZE, 0xFF40506C);

			renderMobPreview(
				graphics,
				entry.mob(),
				boxX + MODEL_BOX_PADDING,
				boxY + MODEL_BOX_PADDING,
				boxX + MODEL_BOX_SIZE - MODEL_BOX_PADDING,
				boxY + MODEL_BOX_SIZE - MODEL_BOX_PADDING,
				selected ? 29 : 26
			);

			int textX = boxX + MODEL_BOX_SIZE + TEXT_LEFT_PADDING;
			int maxTextWidth = x + CARD_WIDTH - TEXT_RIGHT_PADDING - textX;
			graphics.text(this.font, trimTextToWidth(entry.name().getString(), maxTextWidth), textX, y + 14, 0xFFE2EBFF, false);
			graphics.text(this.font, getCardStatus(entry), textX, y + 34, 0xFFB9C7DE, false);
		}

		graphics.disableScissor();
		renderScrollbar(graphics, visibleWidth);
	}

	private void renderMobPreview(GuiGraphicsExtractor graphics, Mob mob, int x0, int y0, int x1, int y1, int size) {
		float spinDegrees = (System.currentTimeMillis() % 12000L) * -0.03F;
		float pitchDegrees = -12.0F;
		float spinRadians = spinDegrees * (float)(Math.PI / 180.0);
		float pitchRadians = pitchDegrees * (float)(Math.PI / 180.0);

		Quaternionf rotation = new Quaternionf().rotateZ((float)Math.PI).rotateY(spinRadians);
		Quaternionf xRotation = new Quaternionf().rotateX(pitchRadians);
		rotation.mul(xRotation);

		mob.setYRot(0.0F);
		mob.setYHeadRot(0.0F);
		mob.setYBodyRot(0.0F);
		mob.setXRot(0.0F);

		EntityRenderState renderState = extractRenderState(mob);
		float previewHeight = Math.max(renderState.boundingBoxHeight, 0.8F);
		float previewWidth = Math.max(renderState.boundingBoxWidth, 0.6F);
		float dominantSize = Math.max(previewHeight, previewWidth);
		float previewScale = getPreviewScaleMultiplier(mob);
		int scaledSize = Math.max(6, Math.min(28, Math.round(size * 0.78F * previewScale / dominantSize)));

		if (renderState instanceof LivingEntityRenderState livingRenderState) {
			livingRenderState.bodyRot = 0.0F;
			livingRenderState.yRot = 0.0F;
			if (livingRenderState.pose != Pose.FALL_FLYING) {
				livingRenderState.xRot = pitchDegrees;
			} else {
				livingRenderState.xRot = 0.0F;
			}

			livingRenderState.boundingBoxWidth = livingRenderState.boundingBoxWidth / livingRenderState.scale;
			livingRenderState.boundingBoxHeight = livingRenderState.boundingBoxHeight / livingRenderState.scale;
			livingRenderState.scale = 1.0F;
		}

		Vector3f translation = new Vector3f(0.0F, previewHeight / 2.0F + 0.03F, 0.0F);
		graphics.entity(
			renderState,
			scaledSize,
			translation,
			rotation,
			xRotation,
			this.leftPos + x0,
			this.topPos + y0,
			this.leftPos + x1,
			this.topPos + y1
		);
	}

	private void renderScrollbar(GuiGraphicsExtractor graphics, int visibleWidth) {
		int trackX = LIST_LEFT + LIST_WIDTH - SCROLLBAR_WIDTH - 4;
		int trackY = LIST_TOP + 6;
		int trackHeight = LIST_HEIGHT - 12;
		graphics.fill(trackX, trackY, trackX + SCROLLBAR_WIDTH, trackY + trackHeight, 0xFF121D2C);
		graphics.outline(trackX, trackY, SCROLLBAR_WIDTH, trackHeight, 0xFF2D425E);

		int maxScroll = getMaxScroll();
		if (maxScroll <= 0) {
			graphics.fill(trackX + 1, trackY + 1, trackX + SCROLLBAR_WIDTH - 1, trackY + trackHeight - 1, 0xFF4A5F85);
			return;
		}

		int thumbHeight = getScrollbarThumbHeight();
		int thumbRange = trackHeight - thumbHeight - 2;
		int thumbY = trackY + 1 + (int)(thumbRange * (this.scrollOffset / maxScroll));
		int thumbColor = this.draggingScrollbar ? 0xFF9AB4FF : 0xFF7088C6;
		graphics.fill(trackX + 1, thumbY, trackX + SCROLLBAR_WIDTH - 1, thumbY + thumbHeight, thumbColor);
	}

	private void renderFooter(GuiGraphicsExtractor graphics) {
		if (this.selectedIndex < 0 || this.selectedIndex >= this.mobEntries.size()) {
			graphics.text(this.font, "No mobs available.", 14, this.imageHeight - 24, 0xFFC7D2E4, false);
			return;
		}

		MobEntry selected = this.mobEntries.get(this.selectedIndex);
		int footerLeft = 14;
		int footerRight = getActionButtonXLocal() - 8;
		String footerText = trimTextToWidth(selected.name().getString(), footerRight - footerLeft);
		graphics.text(this.font, footerText, footerLeft, this.imageHeight - 32, 0xFFDCE7FB, false);
		graphics.text(this.font, getFooterStatus(selected), footerLeft, this.imageHeight - 20, 0xFFAFBDD3, false);
		renderActionButton(graphics, selected);
	}

	private void renderTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		for (int index = 0; index < this.mobEntries.size(); index++) {
			if (isMouseOverCard(index, mouseX, mouseY)) {
				MobEntry entry = this.mobEntries.get(index);
				graphics.setComponentTooltipForNextFrame(
					this.font,
					List.of(
						entry.name()
					),
					mouseX,
					mouseY
				);
				return;
			}
		}
	}

	private int getCollectedCount() {
		return ClientMemoryState.getClaimedCount();
	}

	private int getMemoryTotal() {
		return this.mobEntries.size();
	}

	private int getRowCount() {
		return (this.mobEntries.size() + CARD_COLUMNS - 1) / CARD_COLUMNS;
	}

	private int getContentHeight() {
		int rows = getRowCount();
		if (rows == 0) {
			return LIST_HEIGHT;
		}

		return rows * CARD_HEIGHT + Math.max(0, rows - 1) * CARD_GAP + 16;
	}

	private int getMaxScroll() {
		return Math.max(0, getContentHeight() - LIST_HEIGHT);
	}

	private double clampScroll(double value) {
		return Math.max(0.0D, Math.min(value, getMaxScroll()));
	}

	private boolean isMouseInsideList(double mouseX, double mouseY) {
		int left = this.leftPos + LIST_LEFT;
		int top = this.topPos + LIST_TOP;
		return mouseX >= left && mouseX < left + LIST_WIDTH && mouseY >= top && mouseY < top + LIST_HEIGHT;
	}

	private boolean isMouseOverCard(int index, double mouseX, double mouseY) {
		int column = index % CARD_COLUMNS;
		int row = index / CARD_COLUMNS;
		int x = this.leftPos + LIST_LEFT + LIST_CONTENT_LEFT + column * (CARD_WIDTH + CARD_GAP);
		int y = this.topPos + LIST_TOP + LIST_CONTENT_TOP + row * (CARD_HEIGHT + CARD_GAP) - (int)this.scrollOffset;

		return mouseX >= x && mouseX < x + CARD_WIDTH && mouseY >= y && mouseY < y + CARD_HEIGHT && isMouseInsideList(mouseX, mouseY);
	}

	private boolean isMouseOverCloseButton(double mouseX, double mouseY) {
		int x = this.leftPos + getCloseButtonXLocal();
		int y = this.topPos + CLOSE_BUTTON_TOP;
		return mouseX >= x && mouseX < x + CLOSE_BUTTON_SIZE && mouseY >= y && mouseY < y + CLOSE_BUTTON_SIZE;
	}

	private int getCloseButtonXLocal() {
		return this.imageWidth - CLOSE_BUTTON_RIGHT - CLOSE_BUTTON_SIZE;
	}

	private void playUiClick(float pitch) {
		if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
			return;
		}

		this.minecraft.level.playLocalSound(
			this.minecraft.player.getX(),
			this.minecraft.player.getY(),
			this.minecraft.player.getZ(),
			SoundEvents.UI_BUTTON_CLICK.value(),
			SoundSource.MASTER,
			0.55F,
			pitch,
			false
		);
	}

	private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
		int x = this.leftPos + LIST_LEFT + LIST_WIDTH - SCROLLBAR_WIDTH - 4;
		int y = this.topPos + LIST_TOP + 6;
		int height = LIST_HEIGHT - 12;
		return mouseX >= x && mouseX < x + SCROLLBAR_WIDTH && mouseY >= y && mouseY < y + height;
	}

	private int getScrollbarTrackYScreen() {
		return this.topPos + LIST_TOP + 6;
	}

	private int getScrollbarTrackHeight() {
		return LIST_HEIGHT - 12;
	}

	private int getScrollbarThumbHeight() {
		int maxScroll = getMaxScroll();
		if (maxScroll <= 0) {
			return getScrollbarTrackHeight() - 2;
		}

		return Math.max(18, (int)(getScrollbarTrackHeight() * (LIST_HEIGHT / (double)getContentHeight())));
	}

	private int getScrollbarThumbYScreen() {
		int trackY = getScrollbarTrackYScreen();
		int thumbHeight = getScrollbarThumbHeight();
		int maxScroll = getMaxScroll();
		if (maxScroll <= 0) {
			return trackY + 1;
		}

		int thumbRange = getScrollbarTrackHeight() - thumbHeight - 2;
		return trackY + 1 + (int)(thumbRange * (this.scrollOffset / maxScroll));
	}

	private void updateScrollFromMouse(double mouseY) {
		int maxScroll = getMaxScroll();
		if (maxScroll <= 0) {
			this.scrollOffset = 0.0D;
			return;
		}

		int trackY = getScrollbarTrackYScreen();
		int thumbHeight = getScrollbarThumbHeight();
		double thumbRange = getScrollbarTrackHeight() - thumbHeight - 2.0D;
		if (thumbRange <= 0.0D) {
			this.scrollOffset = 0.0D;
			return;
		}

		double thumbTop = Math.max(trackY + 1.0D, Math.min(mouseY - thumbHeight / 2.0D, trackY + 1.0D + thumbRange));
		double progress = (thumbTop - (trackY + 1.0D)) / thumbRange;
		this.scrollOffset = clampScroll(progress * maxScroll);
	}

	private void updateCursor(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		this.actionButtonHovered = isMouseOverActionButton(mouseX, mouseY);
		if (this.draggingScrollbar || isMouseOverScrollbar(mouseX, mouseY)) {
			graphics.requestCursor(CursorTypes.RESIZE_NS);
			return;
		}

		if (isMouseOverCloseButton(mouseX, mouseY)) {
			graphics.requestCursor(CursorTypes.POINTING_HAND);
			return;
		}

		for (int index = 0; index < this.mobEntries.size(); index++) {
			if (isMouseOverCard(index, mouseX, mouseY)) {
				graphics.requestCursor(CursorTypes.POINTING_HAND);
				return;
			}
		}

		if (this.actionButtonHovered && canClaimSelectedMemory()) {
			graphics.requestCursor(CursorTypes.POINTING_HAND);
		}
	}

	private String trimTextToWidth(String text, int maxWidth) {
		if (maxWidth <= 0 || this.font.width(text) <= maxWidth) {
			return text;
		}

		String ellipsis = "...";
		int ellipsisWidth = this.font.width(ellipsis);
		if (ellipsisWidth >= maxWidth) {
			return ellipsis;
		}

		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < text.length(); index++) {
			String candidate = builder.toString() + text.charAt(index);
			if (this.font.width(candidate) + ellipsisWidth > maxWidth) {
				break;
			}
			builder.append(text.charAt(index));
		}

		if (builder.length() == 0) {
			return ellipsis;
		}

		return builder + ellipsis;
	}

	private float getPreviewScaleMultiplier(Mob mob) {
		String path = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getPath();
		return switch (path) {
			case "ender_dragon" -> 0.22F;
			case "ghast" -> 0.52F;
			default -> 1.0F;
		};
	}

	private void renderActionButton(GuiGraphicsExtractor graphics, MobEntry entry) {
		int x = getActionButtonXLocal();
		int y = ACTION_BUTTON_Y;
		boolean claimable = canClaimMemory(entry);
		int background = claimable ? (this.actionButtonHovered ? 0xFF587B36 : 0xFF44622A) : 0xFF2B3442;
		int border = claimable ? 0xFFA9D27E : 0xFF55647C;
		if (isClaimed(entry)) {
			background = 0xFF2F4334;
			border = 0xFF7EB18A;
		}
		graphics.fill(x, y, x + ACTION_BUTTON_WIDTH, y + ACTION_BUTTON_HEIGHT, background);
		graphics.outline(x, y, ACTION_BUTTON_WIDTH, ACTION_BUTTON_HEIGHT, border);
		graphics.centeredText(this.font, Component.literal(getActionButtonLabel(entry)), x + ACTION_BUTTON_WIDTH / 2, y + 5, 0xFFF1F6FD);
	}

	private String getActionButtonLabel(MobEntry entry) {
		if (isClaimed(entry)) {
			return "RECORDED";
		}
		if (isUnlocked(entry)) {
			return "RECORD";
		}
		return "LOCKED";
	}

	private String getCardStatus(MobEntry entry) {
		if (isClaimed(entry)) {
			return "Recorded";
		}
		if (isUnlocked(entry)) {
			return "Ready";
		}
		return "Unknown";
	}

	private String getFooterStatus(MobEntry entry) {
		if (isClaimed(entry)) {
			return "Memory recorded. Reward claimed.";
		}
		if (isUnlocked(entry)) {
			return "Ready to record. Reward: " + OrbisMemoryRewards.getExperienceReward(entry.type()) + " XP";
		}
		return "Look at this mob in the world to unlock its memory.";
	}

	private MobEntry getSelectedEntry() {
		return this.selectedIndex >= 0 && this.selectedIndex < this.mobEntries.size() ? this.mobEntries.get(this.selectedIndex) : null;
	}

	private boolean isUnlocked(MobEntry entry) {
		return ClientMemoryState.isUnlocked(entry.id().toString());
	}

	private boolean isClaimed(MobEntry entry) {
		return ClientMemoryState.isClaimed(entry.id().toString());
	}

	private boolean canClaimMemory(MobEntry entry) {
		return isUnlocked(entry) && !isClaimed(entry);
	}

	private boolean canClaimSelectedMemory() {
		MobEntry entry = getSelectedEntry();
		return entry != null && canClaimMemory(entry);
	}

	private boolean isMouseOverActionButton(double mouseX, double mouseY) {
		int x = this.leftPos + getActionButtonXLocal();
		int y = this.topPos + ACTION_BUTTON_Y;
		return mouseX >= x && mouseX < x + ACTION_BUTTON_WIDTH && mouseY >= y && mouseY < y + ACTION_BUTTON_HEIGHT;
	}

	private int getActionButtonXLocal() {
		return this.imageWidth - ACTION_BUTTON_RIGHT - ACTION_BUTTON_WIDTH;
	}

	private static EntityRenderState extractRenderState(Mob entity) {
		EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		EntityRenderer<? super Mob, ?> renderer = entityRenderDispatcher.getRenderer(entity);
		EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
		renderState.shadowPieces.clear();
		renderState.outlineColor = 0;
		return renderState;
	}

	private record MobEntry(EntityType<?> type, Mob mob, Component name, Identifier id) {
	}
}
