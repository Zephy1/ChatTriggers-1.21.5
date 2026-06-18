package com.chattriggers.ctjs.typing

val manualRoots = setOf(
    "java.awt.Color",
    "java.util.ArrayList",
    "java.util.HashMap",
    "com.chattriggers.ctjs.api.client.Keyboard",
    "gg.essential.universal.UKeyboard",
    "net.minecraft.world.InteractionHand",
    "org.lwjgl.opengl.GL11",
    "org.lwjgl.opengl.GL12",
    "org.lwjgl.opengl.GL13",
    "org.lwjgl.opengl.GL14",
    "org.lwjgl.opengl.GL15",
    "org.lwjgl.opengl.GL20",
    "org.lwjgl.opengl.GL21",
    "org.lwjgl.opengl.GL30",
    "org.lwjgl.opengl.GL31",
    "org.lwjgl.opengl.GL32",
    "org.lwjgl.opengl.GL33",
    "org.lwjgl.opengl.GL40",
    "org.lwjgl.opengl.GL41",
    "org.lwjgl.opengl.GL42",
    "org.lwjgl.opengl.GL43",
    "org.lwjgl.opengl.GL44",
    "org.lwjgl.opengl.GL45",
    "org.spongepowered.asm.mixin.injection.callback.CallbackInfo",
)

private val providedTypes = mutableMapOf(
    "InteractionHand" to "net.minecraft.world.InteractionHand",
    "UKeyboard" to "gg.essential.universal.UKeyboard",

    "Client" to "com.chattriggers.ctjs.api.client.Client",
    "CPS" to "com.chattriggers.ctjs.api.client.CPS",
    "CTPlayer" to "com.chattriggers.ctjs.api.client.CTPlayer",
    "CTSound" to "com.chattriggers.ctjs.api.client.CTSound",
    "FileLib" to "com.chattriggers.ctjs.api.client.FileLib",
    "KeyBind" to "com.chattriggers.ctjs.api.client.KeyBind",
    "Keyboard" to "com.chattriggers.ctjs.api.client.Keyboard",
    "MathLib" to "com.chattriggers.ctjs.api.client.MathLib",
    "Settings" to "com.chattriggers.ctjs.api.client.Settings",

    "Commands" to "com.chattriggers.ctjs.api.commands.DynamicCommands",
    "RootCommand" to "com.chattriggers.ctjs.api.commands.RootCommand",

    "CTBlockEntity" to "com.chattriggers.ctjs.api.entity.CTBlockEntity",
    "CTEntity" to "com.chattriggers.ctjs.api.entity.CTEntity",
    "CTLivingEntity" to "com.chattriggers.ctjs.api.entity.CTLivingEntity",
    "CTParticle" to "com.chattriggers.ctjs.api.entity.CTParticle",
    "CTTeam" to "com.chattriggers.ctjs.api.entity.CTTeam",
    "PlayerInteraction" to "com.chattriggers.ctjs.api.entity.PlayerInteraction",
    "PlayerMP" to "com.chattriggers.ctjs.api.entity.PlayerMP",

    "Action" to "com.chattriggers.ctjs.api.inventory.action.Action",
    "ClickAction" to "com.chattriggers.ctjs.api.inventory.action.ClickAction",
    "DragAction" to "com.chattriggers.ctjs.api.inventory.action.DragAction",
    "DropAction" to "com.chattriggers.ctjs.api.inventory.action.DropAction",
    "KeyAction" to "com.chattriggers.ctjs.api.inventory.action.KeyAction",

    "NBT" to "com.chattriggers.ctjs.api.inventory.nbt.NBT",
    "NBTBase" to "com.chattriggers.ctjs.api.inventory.nbt.NBTBase",
    "NBTTagCompound" to "com.chattriggers.ctjs.api.inventory.nbt.NBTTagCompound",
    "NBTTagList" to "com.chattriggers.ctjs.api.inventory.nbt.NBTTagList",

    "CTClickType" to "com.chattriggers.ctjs.api.inventory.CTClickType",
    "CTItem" to "com.chattriggers.ctjs.api.inventory.CTItem",
    "CTSlot" to "com.chattriggers.ctjs.api.inventory.CTSlot",
    "Inventory" to "com.chattriggers.ctjs.api.inventory.Inventory",
    "ItemType" to "com.chattriggers.ctjs.api.inventory.ItemType",

    "ChatLib" to "com.chattriggers.ctjs.api.message.ChatLib",
    "TextComponent" to "com.chattriggers.ctjs.api.message.TextComponent",

    "GradientGUIRenderState" to "com.chattriggers.ctjs.api.render.renderstates.GradientGUIRenderState",
    "GUIRenderState" to "com.chattriggers.ctjs.api.render.renderstates.GUIRenderState",
    "TexturedGUIRenderState" to "com.chattriggers.ctjs.api.render.renderstates.TexturedGUIRenderState",

    "AdvancedDrawContext" to "com.chattriggers.ctjs.api.render.AdvancedDrawContext",
    "BaseGUIRenderer" to "com.chattriggers.ctjs.api.render.BaseGUIRenderer",
    "BaseWorldRenderer" to "com.chattriggers.ctjs.api.render.BaseWorldRenderer",
    "Book" to "com.chattriggers.ctjs.api.render.Book",
    "CTPlayerRenderer" to "com.chattriggers.ctjs.api.render.CTPlayerRenderer",
    "Display" to "com.chattriggers.ctjs.api.render.Display",
    "DrawMode" to "com.chattriggers.ctjs.api.render.DrawMode",
    "Gui" to "com.chattriggers.ctjs.api.render.Gui",
    "GUIRenderer" to "com.chattriggers.ctjs.api.render.GUIRenderer",
    "HudRenderLayer" to "com.chattriggers.ctjs.api.render.HudRenderLayer",
    "Image" to "com.chattriggers.ctjs.api.render.Image",
    "PipelineBuilder" to "com.chattriggers.ctjs.api.render.PipelineBuilder",
    "Rectangle" to "com.chattriggers.ctjs.api.render.Rectangle",
    "RenderLayers" to "com.chattriggers.ctjs.api.render.RenderLayers",
    "RenderPipelines" to "com.chattriggers.ctjs.api.render.RenderPipelines",
    "RenderSnippet" to "com.chattriggers.ctjs.api.render.RenderSnippet",
    "RenderUtils" to "com.chattriggers.ctjs.api.render.RenderUtils",
    "Shape" to "com.chattriggers.ctjs.api.render.Shape",
    "TemporaryTextureAllocator" to "com.chattriggers.ctjs.api.render.TemporaryTextureAllocator",
    "Text" to "com.chattriggers.ctjs.api.render.Text",
    "Toast" to "com.chattriggers.ctjs.api.render.Toast",
    "UMatrixStack" to "com.chattriggers.ctjs.api.render.UMatrixStack",
    "UScreen" to "com.chattriggers.ctjs.api.render.UScreen",
    "VertexFormat" to "com.chattriggers.ctjs.api.render.VertexFormat",
    "WorldRenderer" to "com.chattriggers.ctjs.api.render.WorldRenderer",

    "CancellableEvent" to "com.chattriggers.ctjs.api.triggers.CancellableEvent",
    "ChatTrigger" to "com.chattriggers.ctjs.api.triggers.ChatTrigger",
    "ClassFilterTrigger" to "com.chattriggers.ctjs.api.triggers.ClassFilterTrigger",
    "CommandTrigger" to "com.chattriggers.ctjs.api.triggers.CommandTrigger",
    "EventTrigger" to "com.chattriggers.ctjs.api.triggers.EventTrigger",
    "RegularTrigger" to "com.chattriggers.ctjs.api.triggers.RegularTrigger",
    "SoundPlayTrigger" to "com.chattriggers.ctjs.api.triggers.SoundPlayTrigger",
    "StepTrigger" to "com.chattriggers.ctjs.api.triggers.StepTrigger",
    "Trigger" to "com.chattriggers.ctjs.api.triggers.Trigger",
    "TriggerType" to "com.chattriggers.ctjs.api.triggers.TriggerType",

    "Vec2f" to "com.chattriggers.ctjs.api.vec.Vec2f",
    "Vec3f" to "com.chattriggers.ctjs.api.vec.Vec3f",
    "Vec3i" to "com.chattriggers.ctjs.api.vec.Vec3i",

    "BlockFace" to "com.chattriggers.ctjs.api.world.block.BlockFace",
    "CTBlock" to "com.chattriggers.ctjs.api.world.block.CTBlock",
    "CTBlockPos" to "com.chattriggers.ctjs.api.world.block.BlockPos",
    "CTBlockType" to "com.chattriggers.ctjs.api.world.block.BlockType",

    "BossBars" to "com.chattriggers.ctjs.api.world.BossBars",
    "CTChunk" to "com.chattriggers.ctjs.api.world.CTChunk",
    "PotionEffect" to "com.chattriggers.ctjs.api.world.PotionEffect",
    "PotionEffectType" to "com.chattriggers.ctjs.api.world.PotionEffectType",
    "Scoreboard" to "com.chattriggers.ctjs.api.world.Scoreboard",
    "Server" to "com.chattriggers.ctjs.api.world.Server",
    "TabList" to "com.chattriggers.ctjs.api.world.TabList",
    "World" to "com.chattriggers.ctjs.api.world.World",

    "Config" to "com.chattriggers.ctjs.api.Config",

    "TriggerRegister" to "com.chattriggers.ctjs.engine.Register",
    "Thread" to "com.chattriggers.ctjs.engine.WrappedThread",
    "Priority" to "com.chattriggers.ctjs.api.triggers.Trigger\$Priority",
    "ChatTriggers" to "com.chattriggers.ctjs.CTJS",
    "Console" to "com.chattriggers.ctjs.engine.Console",

    "GL11" to "org.lwjgl.opengl.GL11",
    "GL12" to "org.lwjgl.opengl.GL12",
    "GL13" to "org.lwjgl.opengl.GL13",
    "GL14" to "org.lwjgl.opengl.GL14",
    "GL15" to "org.lwjgl.opengl.GL15",
    "GL20" to "org.lwjgl.opengl.GL20",
    "GL21" to "org.lwjgl.opengl.GL21",
    "GL30" to "org.lwjgl.opengl.GL30",
    "GL31" to "org.lwjgl.opengl.GL31",
    "GL32" to "org.lwjgl.opengl.GL32",
    "GL33" to "org.lwjgl.opengl.GL33",
    "GL40" to "org.lwjgl.opengl.GL40",
    "GL41" to "org.lwjgl.opengl.GL41",
    "GL42" to "org.lwjgl.opengl.GL42",
    "GL43" to "org.lwjgl.opengl.GL43",
    "GL44" to "org.lwjgl.opengl.GL44",
    "GL45" to "org.lwjgl.opengl.GL45",
)

val prologue = """
    /// <reference no-default-lib="true" />
    /// <reference lib="es2015" />
    export {};
    
    declare interface String {
      addFormatting(): string;
      addColor(): string;
      removeFormatting(): string;
      replaceFormatting(): string;
    }
    
    declare interface Number {
      easeOut(to: number, speed: number, jump: number): number;
      easeColor(to: number, speed: number, jump: number): java.awt.Color;
    }

    interface RegisterTypes {
      // client
      chat(...args: (string | unknown)[]): com.chattriggers.ctjs.api.triggers.ChatTrigger;
      actionBar(...args: (string | unknown)[]): com.chattriggers.ctjs.api.triggers.ChatTrigger;
      tick(ticksElapsed: number): com.chattriggers.ctjs.api.triggers.Trigger;
      step(stepsElapsed: number): com.chattriggers.ctjs.api.triggers.StepTrigger;
      gameLoad(): com.chattriggers.ctjs.api.triggers.Trigger;
      gameUnload(): com.chattriggers.ctjs.api.triggers.Trigger;
      messageSent(message: string, event: CancellableEvent): com.chattriggers.ctjs.api.triggers.EventTrigger;
      itemTooltip(lore: TextComponent[], item: Item, event: org.spongepowered.asm.mixin.injection.callback.CallbackInfo): com.chattriggers.ctjs.api.triggers.EventTrigger;
      playerInteract(interaction: com.chattriggers.ctjs.api.entity.PlayerInteraction, interactionTarget: Entity | Block | Item, event: CancellableEvent): com.chattriggers.ctjs.api.triggers.EventTrigger;
      packetSent(packet: net.minecraft.network.packet.Packet<unknown>, event: org.spongepowered.asm.mixin.injection.callback.CallbackInfo): com.chattriggers.ctjs.api.triggers.PacketTrigger;
      packetReceived(packet: net.minecraft.network.packet.Packet<unknown>, event: CancellableEvent): com.chattriggers.ctjs.api.triggers.PacketTrigger;
      serverConnect(): com.chattriggers.ctjs.api.triggers.Trigger;
      serverDisconnect(): com.chattriggers.ctjs.api.triggers.Trigger;
      dropItem(item: Item, entireStack: boolean, event: org.spongepowered.asm.mixin.injection.callback.CallbackInfo): com.chattriggers.ctjs.api.triggers.EventTrigger;
      
      // gui
      guiOpened(screen: net.minecraft.client.gui.screen.Screen, event: org.spongepowered.asm.mixin.injection.callback.CallbackInfo): com.chattriggers.ctjs.api.triggers.EventTrigger;
      guiClosed(screen: net.minecraft.client.gui.screen.Screen): com.chattriggers.ctjs.api.triggers.Trigger;
      clicked(moseX: number, mouseY: number, button: number, isPressed: boolean): com.chattriggers.ctjs.api.triggers.Trigger;
      scrolled(mouseX: number, mouseY: number, scrollDelta: number): com.chattriggers.ctjs.api.triggers.Trigger;
      dragged(mouseXDelta: number, mouseYDelta: number, mouseX: number, mouseY: number, mouseButton: number): com.chattriggers.ctjs.api.triggers.Trigger;
      guiKey(char: String, keyCode: number, screen: net.minecraft.client.gui.screen.Screen, event: CancellableEvent): com.chattriggers.ctjs.api.triggers.EventTrigger;
      guiMouseClick(mouseX: number, mouseY: number, mouseButton: number, isPressed: boolean, screen: net.minecraft.client.gui.screen.Screen, event: CancellableEvent): com.chattriggers.ctjs.api.triggers.EventTrigger;
      guiMouseDrag(mouseXDelta: number, mouseYDelta: number, mouseX: number, mouseY: number, mouseButton: number, screen: net.minecraft.client.gui.screen.Screen, event: CancellableEvent): com.chattriggers.ctjs.api.triggers.EventTrigger;
      
      // rendering
      preRenderWorld(partialTicks: number): com.chattriggers.ctjs.api.triggers.Trigger;
      postRenderWorld(partialTicks: number): com.chattriggers.ctjs.api.triggers.Trigger;
      preRenderGui(mouseX: number, mouseY: number, screen: net.minecraft.client.gui.screen.Screen, partialTicks: number, drawContext: net.minecraft.client.gui.DrawContext): com.chattriggers.ctjs.api.triggers.Trigger;
      postRenderGui(mouseX: number, mouseY: number, screen: net.minecraft.client.gui.screen.Screen, partialTicks: number, drawContext: net.minecraft.client.gui.DrawContext): com.chattriggers.ctjs.api.triggers.Trigger;
      renderBlockHighlight(position: BlockPos, event: CancellableEvent): com.chattriggers.ctjs.api.triggers.EventTrigger;
      renderBlockEntity(blockEntity: BlockEntity, partialTicks: number, event: CancellableEvent): com.chattriggers.ctjs.api.triggers.RenderBlockEntityTrigger;
      renderEntity(entity: Entity, partialTicks: number, event: CancellableEvent): com.chattriggers.ctjs.api.triggers.RenderEntityTrigger;
      renderPlayerList(event: org.spongepowered.asm.mixin.injection.callback.CallbackInfo): com.chattriggers.ctjs.api.triggers.EventTrigger;
      renderHudOverlay(): com.chattriggers.ctjs.api.triggers.Trigger;
      renderScreenOverlay(drawContext: net.minecraft.client.gui.DrawContext, partialTicks: number): com.chattriggers.ctjs.api.triggers.Trigger;
      renderHideableScreenOverlay(drawContext: net.minecraft.client.gui.DrawContext, partialTicks: number): com.chattriggers.ctjs.api.triggers.Trigger;
      
      // world
      worldLoad(): com.chattriggers.ctjs.api.triggers.Trigger;
      worldUnload(): com.chattriggers.ctjs.api.triggers.Trigger;
      soundPlay(position: com.chattriggers.ctjs.api.vec.Vec3f, name: string, volume: number, pitch: number, category: net.minecraft.sound.SoundCategory, event: org.spongepowered.asm.mixin.injection.callback.CallbackInfo): com.chattriggers.ctjs.api.triggers.SoundPlayTrigger;
      spawnParticle(particle: Particle, event: org.spongepowered.asm.mixin.injection.callback.CallbackInfo): com.chattriggers.ctjs.api.triggers.EventTrigger;
      entityDamage(entity: Entity, attacker: PlayerMP): com.chattriggers.ctjs.api.triggers.Trigger;
      entityDeath(entity: Entity): com.chattriggers.ctjs.api.triggers.Trigger;
      
      // misc
      command(...args: string[]): com.chattriggers.ctjs.api.triggers.CommandTrigger;
    }
  
    declare global {
      const Java: {
        /**
         * Returns the Java Class or Package given by name. If you want to
         * enforce the name is a class, use Java.class() instead.
         */
        type(name: string): java.lang.Package | java.lang.Class<any>;
  
        /**
         * Returns the Java Class given by `className`. Throws an error if the
         * name is not a valid class name.
         */
        class(className: string): java.lang.Class<any>;
      };

      /**
       * Runs `func` in a Java synchronized() block with `lock` as the synchronizer
       */
      function sync(func: () => void, lock: unknown): void;
  
      /**
       * Runs `func` after `delayInMs` milliseconds. A new thread is spawned to accomplish
       * this, which means this function is asynchronous. If you want to avoid the Thread
       * instantiation, use `Client.scheduleTask(delayInTicks, func)`.
       */
      function setTimeout(func: () => void, delayInMs: number): void;

      const ArrayList: typeof java.util.ArrayList;
      interface ArrayList<T> extends java.util.ArrayList<T> {}
      const HashMap: typeof java.util.HashMap;
      interface HashMap<K, V> extends java.util.HashMap<K, V> {}
      
${providedTypes.entries.joinToString("") { (name, type) ->
    "const $name: typeof $type;\ninterface $name extends $type {}\n"
}.prependIndent("      ")}

      /**
       * Registers a new trigger and returns it.
       */
      function register<T extends keyof RegisterTypes>(
        name: T, 
        cb: (...args: Parameters<RegisterTypes[T]>) => void,
      ): ReturnType<RegisterTypes[T]>;

      /**
       * Cancels the given event
       */
      function cancel(event: CancellableEvent | org.spongepowered.asm.mixin.injection.callback.CallbackInfo): void;

      /**
       * Creates a custom trigger. `name` can be used as the first argument of a
       * subsequent call to `register`. Returns an object that can be used to
       * invoke the trigger.
       */
      function createCustomTrigger(name: string): { trigger(...args: unknown[]) };
      
      function easeOut(start: number, finish: number, speed: number, jump?: number): number;
      function easeColor(start: number, finish: number, speed: number, jump?: number): java.awt.Color;

      function print(message: string, color?: java.awt.Color): void;
      function println(message: string, color?: java.awt.Color, end?: string): void;

      const console: {
        assert(condition: boolean, message: string): void;
        clear(): void;
        count(label?: string): void;
        debug(args: unknown[]): void;
        dir(obj: object): void;
        dirxml(obj: object): void;
        error(...args: unknown[]): void;
        group(...args: unknown[]): void;
        groupCollapsed(...args: unknown[]): void;
        groupEnd(...args: unknown[]): void;
        info(...args: unknown[]): void;
        log(...args: unknown[]): void;
        table(data: object, columns?: string[]): void;
        time(label?: string): void;
        timeEnd(label?: string): void;
        trace(...args: unknown[]): void;
        warn(...args: unknown[]): void;
      };
""".trimIndent()
