package gay.aurum.worldbordercolor.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import com.mojang.blaze3d.systems.RenderSystem;

import static gay.aurum.worldbordercolor.client.WorldbordercolorClient.BORDERCOLOR;

@Mixin(WorldRenderer.class)
public class WorldRenderMixin {

    @Shadow
    private ClientWorld world;

    @ModifyArgs(method = "renderWorldBorder", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V",remap = false))
    private void injectcolor(Args args){
        int aaa = world.getWorldBorder().getStage().getColor();
        float r;
        float g;
        float b;
        float a;
        if(aaa == 4259712){
            float[] col = BORDERCOLOR.unpack(BORDERCOLOR.growColor);
            System.out.println(col[0]+" "+ col[1]+" "+ col[2]);
            r=col[0];
            g=col[1];
            b=col[2];
        }else if (aaa == 0xFF3030){
            float[] col = BORDERCOLOR.unpack(BORDERCOLOR.shrinkColor);
            System.out.println(col[0]+" "+ col[1]+" "+ col[2]);
            r=col[0];
            g=col[1];
            b=col[2];
        }else{
            float[] col = BORDERCOLOR.unpack(BORDERCOLOR.baseColor);
            System.out.println(col[0]+" "+ col[1]+" "+ col[2]);
            r=col[0];
            g=col[1];
            b=col[2];
        }

        args.set(0,r);
        args.set(1,g);
        args.set(2,b);
        a = args.get(3);
        if(BORDERCOLOR.overRideAlpha){
            a = BORDERCOLOR.alpha;
        }
        args.set(3,a);
    }

    @Inject(method = "renderWorldBorder", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableCull()V",remap = false))
    private void changeblendmode(Camera camera, CallbackInfo ci){
        if(BORDERCOLOR.noBlend || BORDERCOLOR.darkBlend){
            RenderSystem.disableBlend();
            if(BORDERCOLOR.darkBlend) {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.DstFactor.SRC_COLOR, GlStateManager.SrcFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.DstFactor.ZERO);
            }
        }
    }

}
