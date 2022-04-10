package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.world.capabilities.item.ItemCapabilityListener;

public class SPDatapackSync {
	private int count;
	private int index;
	private SPDatapackSync.Type type;
	private CompoundTag[] tags;
	
	public SPDatapackSync() {
		this(0, SPDatapackSync.Type.WEAPON);
	}
	
	public SPDatapackSync(int count, SPDatapackSync.Type type) {
		this.count = count;
		this.index = 0;
		this.type = type;
		this.tags = new CompoundTag[count];
	}
	
	public void write(CompoundTag tag) {
		this.tags[this.index] = tag;
		this.index++;
	}
	
	public CompoundTag[] getTags() {
		return this.tags;
	}
	
	public SPDatapackSync.Type getType() {
		return this.type;
	}
	
	public static SPDatapackSync fromBytes(FriendlyByteBuf buf) {
		SPDatapackSync msg = new SPDatapackSync(buf.readInt(), SPDatapackSync.Type.values()[buf.readInt()]);
		for (int i = 0; i < msg.count; i++) {
			msg.tags[i] = buf.readNbt();
		}
		
		return msg;
	}
	
	public static void toBytes(SPDatapackSync msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.count);
		buf.writeInt(msg.type.ordinal());
		for (CompoundTag tag : msg.tags) {
			buf.writeNbt(tag);
		}
	}
	
	public static void handle(SPDatapackSync msg, Supplier<NetworkEvent.Context> ctx) {
		ItemCapabilityListener.processServerData(msg);
		ctx.get().setPacketHandled(true);
	}
	
	public static enum Type {
		ARMOR, WEAPON
	}
}