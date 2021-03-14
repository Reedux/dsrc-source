package script.conversation;

import script.*;
import script.library.ai_lib;
import script.library.chat;
import script.library.utils;

import java.util.Objects;

public class tcg_vendor extends script.base_script {
    public tcg_vendor() {
    }

    public static String c_stringFile = "conversation/tcg_vendor";
    public static final boolean configSetting = utils.checkConfigFlag("GameServer", "tcgVendorEnable");

    public void tcg_vendor_action_showTokenVendorUI(obj_id player, obj_id npc) {
        dictionary d = new dictionary();
        d.put("player", player);
        messageTo(npc, "showInventorySUI", d, 0, false);
    }

    public int OnInitialize(obj_id self) throws InterruptedException {
        if ((!isTangible(self)) || (isPlayer(self))) {
            detachScript(self, "conversation.tcg_vendor");
        }
        if (configSetting) {
            setCondition(self, CONDITION_CONVERSABLE);
        }
        return SCRIPT_CONTINUE;
    }

    public int OnAttach(obj_id self) throws InterruptedException {
        if (configSetting) {
            setCondition(self, CONDITION_CONVERSABLE);
        }
        return SCRIPT_CONTINUE;
    }

    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info menuInfo) throws InterruptedException {
        if (!hasCondition(self, CONDITION_CONVERSABLE)) {
            return SCRIPT_OVERRIDE;
        }
        int menu = menuInfo.addRootMenu(menu_info_types.CONVERSE_START, null);
        menu_info_data menuInfoData = menuInfo.getMenuItemById(menu);
        Objects.requireNonNull(menuInfoData).setServerNotify(false);
        setCondition(self, CONDITION_CONVERSABLE);
        return SCRIPT_CONTINUE;
    }

    public int OnIncapacitated(obj_id self, obj_id killer) throws InterruptedException {
        clearCondition(self, CONDITION_CONVERSABLE);
        detachScript(self, "conversation.tcg_vendor");
        return SCRIPT_CONTINUE;
    }

    public boolean npcStartConversation(obj_id player, obj_id npc, String convoName, string_id greetingId, prose_package greetingProse, string_id[] responses) throws InterruptedException {
        Object[] objects = new Object[responses.length];
        System.arraycopy(responses, 0, objects, 0, responses.length);
        return npcStartConversation(player, npc, convoName, greetingId, greetingProse, objects);
    }

    public int OnStartNpcConversation(obj_id self, obj_id player) throws InterruptedException {
        if (ai_lib.isInCombat(self) || ai_lib.isInCombat(player)) {
            return SCRIPT_OVERRIDE;
        }
        tcg_vendor_action_showTokenVendorUI(player, self);
        string_id message = new string_id(c_stringFile, "s_4");
        chat.chat(self, player, message);
        return SCRIPT_CONTINUE;
    }

    public int OnNpcConversationResponse(obj_id self, String conversationId, obj_id player, string_id response) throws InterruptedException {
        if (!conversationId.equals("tcg_vendor")) {
            return SCRIPT_CONTINUE;
        }
        chat.chat(self, "Error:  Fell through all branches and responses for OnNpcConversationResponse.");
        utils.removeScriptVar(player, "conversation.tcg_vendor.branchId");
        return SCRIPT_CONTINUE;
    }
}
