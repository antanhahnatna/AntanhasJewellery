package net.botwithus;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

import java.util.Arrays;
import java.util.LinkedList;

public class AntanhasJewelleryGraphicsContext extends ScriptGraphicsContext {

    private AntanhasJewellery script;

    public AntanhasJewelleryGraphicsContext(ScriptConsole scriptConsole, AntanhasJewellery script) {
        super(scriptConsole);
        this.script = script;
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("Antanha's jewellery", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.getValue())) {
                if (ImGui.BeginTabItem("Main", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Script state: " + script.getBotState());
                    ImGui.BeginDisabled(script.getBotState() != AntanhasJewellery.BotState.STOPPED);
                    if (ImGui.Button("Start")) {
                        //button has been clicked
                        script.setBotState(AntanhasJewellery.BotState.SETUP);
                        script.logNames = new LinkedList<>();
                        script.logAmounts = new LinkedList<>();
                        script.experienceGained = 0;
                        script.startingTime = System.currentTimeMillis();
                        script.saveConfiguration();
                    }
                    ImGui.EndDisabled();
                    ImGui.SameLine();
                    ImGui.BeginDisabled(script.getBotState() == AntanhasJewellery.BotState.STOPPED);
                    if (ImGui.Button("Stop")) {
                        //has been clicked
                        script.setBotState(AntanhasJewellery.BotState.STOPPED);
                        script.timeScriptWasLastActive = System.currentTimeMillis();
                    }
                    ImGui.EndDisabled();
                    ImGui.Separator();
                    ImGui.BeginDisabled(script.getBotState() != AntanhasJewellery.BotState.STOPPED);

                    String[] categories3 = Arrays.stream(AntanhasJewellery.JewelToSmelt.class.getEnumConstants()).map(Enum::name).toArray(String[]::new);
                    int indexOfCurrentlySelectedJewelToSmeltEnum = script.getJewelToSmelt().ordinal();
                    script.setJewelToSmelt(AntanhasJewellery.JewelToSmelt.values()[ImGui.Combo("Select a jewel", indexOfCurrentlySelectedJewelToSmeltEnum, categories3)]);
                    ImGui.EndDisabled();
                    ImGui.Separator();
                    ImGui.Text("Instructions:");
                    ImGui.Text("This script assumes all your gold bars are in the metal bank. Set your last bank preset according to what you want to craft (e.g. 28 rubies), then start the script in Fort Forinthry or Artisan's Workshop.");
                    ImGui.EndTabItem();
                }
                if (ImGui.BeginTabItem("Stats", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text(script.logString());
                    ImGui.EndTabItem();
                }
                ImGui.EndTabBar();
            }
            ImGui.End();
        }

    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
}
