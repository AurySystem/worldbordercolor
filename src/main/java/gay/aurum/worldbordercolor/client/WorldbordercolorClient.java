package gay.aurum.worldbordercolor.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class WorldbordercolorClient implements ClientModInitializer {
    public static String MOD_ID = "worldbordercolor";
    public static BorderOptions BORDERCOLOR = new BorderOptions();

    private static Gson processer = new GsonBuilder().setLenient().setPrettyPrinting().create();

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void reload(ResourceManager manager) {
                apply(manager);
            }

            @Override
            public Identifier getFabricId() {
                return new Identifier(MOD_ID, "worldborder");
            }

            public void apply(ResourceManager manager) {

                for(Identifier id : manager.findResources("worldborderconfig", path -> path.endsWith(".json"))) {
                    try(InputStream stream = manager.getResource(id).getInputStream()) {
                        String contents = IOUtils.toString(stream, StandardCharsets.UTF_8);
                        JsonElement ele = JsonHelper.deserialize(processer, contents, JsonElement.class);
                        if (ele != null && ele.isJsonObject()) {
                            JsonObject obj = ele.getAsJsonObject();

                            BORDERCOLOR = new BorderOptions(JsonHelper.getString(obj,"blending","on"),
                                    JsonHelper.getBoolean(obj,"customtrans",true),
                                    JsonHelper.getString(obj,"color","#20A0FF").replace("#",""),
                                    JsonHelper.getString(obj,"colorshrink","#FF3030").replace("#",""),
                                    JsonHelper.getString(obj,"colorgrow","#40FF80").replace("#",""),
                                    JsonHelper.getInt(obj,"trans",255)
                            );
                        }
                    } catch(Exception e) {
                        System.out.println(MOD_ID+ " aaah "+ e.toString());
                    }
                }
            }
        });
    }

    public static class BorderOptions{
        public boolean darkBlend = false;
        public boolean noBlend = false;
        public boolean overRideAlpha;
        public int baseColor;
        public int shrinkColor;
        public int growColor;
        public float alpha;
        BorderOptions(){
            this("on",false,"20A0FF","FF3030","40FF80",255);
        }

        BorderOptions(String blendType, boolean overRideAlpha, String baseColor, @Nullable String shrinkColor, @Nullable String growColor, @Nullable int alpha){
            if(blendType.equals("dark")){
                this.darkBlend = true;
            }
            if(blendType.equals("off")){
                this.noBlend = true;
            }
            this.overRideAlpha = overRideAlpha;
            this.baseColor = this.parser(baseColor,0x00fafaff);
            this.shrinkColor = this.parser(shrinkColor,this.baseColor);
            this.growColor = this.parser(growColor,this.baseColor);
            this.alpha = (float)alpha/255;
        }

        private int parser(@Nullable String hex,int fallback){
            int i = 0;
            if(hex != null){
                try{
                    i = Integer.parseInt(hex,16);
                }catch (NumberFormatException e){
                    i = fallback;
                }
            }else i = fallback;
            return i;
        }
        public float[] unpack(int packedRGBA){
            int traverse = packedRGBA;
            float b = (float)(traverse & 0xFF) / 255.0f;
            traverse = traverse >> 8;
            float g = (float)(traverse & 0xFF) / 255.0f;
            traverse = traverse >> 8;
            float r = (float)(traverse & 0xFF) / 255.0f;

            return new float[]{r, g, b};
        }
    }
}
