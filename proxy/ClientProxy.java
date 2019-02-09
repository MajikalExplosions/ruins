package majikalexplosions.ruins.proxy;

import majikalexplosions.ruins.RuinsMain;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerItemModel(Item item, int meta, String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(RuinsMain.MOD_ID + ":" + id, "inventory"));
	}
}
