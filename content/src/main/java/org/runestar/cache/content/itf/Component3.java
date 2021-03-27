package org.runestar.cache.content.itf;

import org.runestar.cache.content.CacheType;
import org.runestar.cache.content.io.Input;

public final class Component3 extends CacheType implements Component {

    public boolean fill = false;
    public boolean isHidden = false;
    public boolean spriteFlipH;
    public boolean _bj = false;
    public boolean spriteTiling = false;
    public boolean modelOrthog = false;
    public boolean spriteFlipV;
    public boolean textShadowed = false;
    public boolean hasListener = false;
    public boolean isDraggable = false;
    public boolean noClickThrough = false;
    public boolean isIf3 = false;
    public int heightAlignment = 0;
    public int lineWid = 1;
    public int color = 0;
    public int scrollHeight = 0;
    public int transparency = 0;
    public int rawHeight = 0;
    public int scrollWidth = 0;
    public int rawWidth = 0;
    public int rawX = 0;
    public int rawY = 0;
    public int parentId = -1;
    public int sequenceId = -1;
    public int modelOffsetX = 0;
    public int modelZoom = 100;
    public int modelAngleZ = 0;
    public int _bf = 0;
    public int spriteId2 = -1;
    public int modelOffsetY = 0;
    public int spriteAngle = 0;
    public int modelId = -1;
    public int outline = 0;
    public int modelAngleY = 0;
    public int modelAngleX = 0;
    public int spriteShadow = 0;
    public int modelType = 1;
    public int textYAlignment = 0;
    public int clickMask = 0;
    public int textLineHeight = 0;
    public int fontId = -1;
    public int textXAlignment = 0;
    public int dragDeadZone = 0;
    public int dragDeadTime = 0;
    public int xAlignment = 0;
    public int clientCode = 0;
    public int id = -1;
    public int widthAlignment = 0;
    public int yAlignment = 0;
    public int type;
    public int[] varTransmitTriggers;
    public int[] statTransmitTriggers;
    public int[] invTransmitTriggers;
    public Object[] onClickRepeat;
    public Object[] onMouseOver;
    public Object[] onDrag;
    public Object[] onDragComplete;
    public Object[] onLoad;
    public Object[] onClick;
    public Object[] onMouseRepeat;
    public Object[] onRelease;
    public Object[] onVarTransmit;
    public Object[] onHold;
    public Object[] onMouseLeave;
    public Object[] onTargetLeave;
    public Object[] onTargetEnter;
    public Object[] onInvTransmit;
    public Object[] onStatTransmit;
    public Object[] onOp;
    public Object[] onScrollWheel;
    public Object[] onTimer;
    public String text = "";
    public String opbase = "";
    public String targetVerb = "";
    public String[] ops;

    @Override public void decode(Input in) {
        if (in.g1s() != -1) return;
        isIf3 = true;
        type = in.g1();
        clientCode = in.g2();
        rawX = in.g2s();
        rawY = in.g2s();
        rawWidth = in.g2();
        rawHeight = type == 9 ? in.g2s() : in.g2();
        widthAlignment = in.g1s();
        heightAlignment = in.g1s();
        xAlignment = in.g1s();
        yAlignment = in.g1s();
        parentId = in.g2();
        parentId = 65535 == parentId ? -1 : (parentId += id & -65536);
        isHidden = in.gbool();
        if (type == 0) {
            scrollWidth = in.g2();
            scrollHeight = in.g2();
            noClickThrough = in.gbool();
        }
        if (type == 5) {
            spriteId2 = in.g4s();
            spriteAngle = in.g2();
            spriteTiling = in.gbool();
            transparency = in.g1();
            outline = in.g1();
            spriteShadow = in.g4s();
            spriteFlipH = in.gbool();
            spriteFlipV = in.gbool();
        }
        if (type == 6) {
            modelType = 1;
            modelId = in.g2m();
            modelOffsetX = in.g2s();
            modelOffsetY = in.g2s();
            modelAngleX = in.g2();
            modelAngleY = in.g2();
            modelAngleZ = in.g2();
            modelZoom = in.g2();
            sequenceId = in.g2m();
            modelOrthog = in.gbool();
            in.g2();
            if (widthAlignment != 0) _bf = in.g2();
            if (0 != heightAlignment) in.g2();
        }
        if (4 == type) {
            fontId = in.g2m();
            text = in.gjstr();
            textLineHeight = in.g1();
            textXAlignment = in.g1();
            textYAlignment = in.g1();
            textShadowed = in.gbool();
            color = in.g4s();
        }
        if (type == 3) {
            color = in.g4s();
            fill = in.gbool();
            transparency = in.g1();
        }
        if (type == 9) {
            lineWid = in.g1();
            color = in.g4s();
            _bj = in.gbool();
        }
        clickMask = in.g3();
        opbase = in.gjstr();
        int n = in.g1();
        if (n > 0) {
            ops = new String[n];
            for (int i = 0; i < n; ++i) {
                ops[i] = in.gjstr();
            }
        }
        dragDeadZone = in.g1();
        dragDeadTime = in.g1();
        isDraggable = in.gbool();
        targetVerb = in.gjstr();
        onLoad = readListener(in);
        onMouseOver = readListener(in);
        onMouseLeave = readListener(in);
        onTargetLeave = readListener(in);
        onTargetEnter = readListener(in);
        onVarTransmit = readListener(in);
        onInvTransmit = readListener(in);
        onStatTransmit = readListener(in);
        onTimer = readListener(in);
        onOp = readListener(in);
        onMouseRepeat = readListener(in);
        onClick = readListener(in);
        onClickRepeat = readListener(in);
        onRelease = readListener(in);
        onHold = readListener(in);
        onDrag = readListener(in);
        onDragComplete = readListener(in);
        onScrollWheel = readListener(in);
        varTransmitTriggers = readListenerTriggers(in);
        invTransmitTriggers = readListenerTriggers(in);
        statTransmitTriggers = readListenerTriggers(in);
    }

    private Object[] readListener(Input in) {
        int count = in.g1();
        if (count == 0) return null;
        Object[] args = new Object[count];
        for(int i = 0; i < count; i++) {
            int n = in.g1();
            if (n == 0) {
                args[i] = in.g4s();
            } else if (n == 1) {
                args[i] = in.gjstr();
            }
        }
        hasListener = true;
        return args;
    }

    private int[] readListenerTriggers(Input in) {
        int count = in.g1();
        if (count == 0) return null;
        int[] var4 = new int[count];
        for(int i = 0; i < count; ++i) {
            var4[i] = in.g4s();
        }
        return var4;
    }
}
