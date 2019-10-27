package waylanderou.almostalltheores;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import waylanderou.almostalltheores.client.TooltipHandler;
import waylanderou.almostalltheores.proxy.ClientProxy;
import waylanderou.almostalltheores.proxy.IProxy;
import waylanderou.almostalltheores.proxy.ServerProxy;

@Mod("almostalltheores")
public class AlmostAllTheOres
{    
	public static final String MODID = "almostalltheores";
	public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

	public AlmostAllTheOres() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOWEST, this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);		

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.spec);

		if(ModList.get().isLoaded("tconstruct")) {
			Config.enableTinkersDefaultMaterials();     	        	
		}
		if(ModList.get().isLoaded("toolsforaatogems")) {
			Config.enableToolsforaatogemsMaterials();       	
		}
		if(ModList.get().isLoaded("extragems")) {
			Config.enableExtraGemsMaterials();
		}
		Config.loadConfig();                               
	}

	private void setup(final FMLCommonSetupEvent event) {    	
		OreGeneration.setup(); 
		proxy.init();
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		TooltipHandler handler = new TooltipHandler();
		IEventBus eventbus = MinecraftForge.EVENT_BUS;
		eventbus.addListener(EventPriority.NORMAL, false, ItemTooltipEvent.class, handler::onToolTip);		   	
	}	

}
