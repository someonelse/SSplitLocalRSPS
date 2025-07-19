package client;

final class Texture extends DrawingArea {

    // Configuration
    public static boolean lowMem = true;
    static boolean aBoolean1462, aBoolean1464 = true;
    private static boolean aBoolean1463;
    public static int anInt1465, textureInt1, textureInt2;

    // Lookup tables
    private static int[] anIntArray1468 = new int[512];
    public static final int[] anIntArray1469 = new int[2048];
    public static int[] anIntArray1470 = new int[2048], anIntArray1471 = new int[2048];
    public static int[] anIntArray1472;
    private static int anInt1473, anInt1477 = 20;
    public static int[][] anIntArrayArray1478, anIntArrayArray1479 = new int[50][], anIntArrayArray1483 = new int[50][];
    public static Background[] aBackgroundArray1474s = new Background[50];
    private static boolean[] aBooleanArray1475 = new boolean[50];
    private static int[] anIntArray1476 = new int[50];
    public static int[] anIntArray1480 = new int[50];
    private static int anInt1481;
    public static int[] anIntArray1482 = new int[0x10000];

    // Temp arrays for sorting
    private static int[] valsX, valsY, valsU, valsV, valsW;

    // Perspective‐texturing constants
    private static final int TEX_SIZE = 64, TEX_MASK = TEX_SIZE - 1, TEX_SHIFT = 6;

    // Static initializer: math & lookup tables
    static {
        for(int i = 1; i < 512; i++) anIntArray1468[i] = 32768 / i;
        for(int i = 1; i < 2048; i++) anIntArray1469[i] = 0x10000 / i;
        for(int i = 0; i < 2048; i++){
            anIntArray1470[i] = (int)(65536D * Math.sin(i * 0.0030679614999999999D));
            anIntArray1471[i] = (int)(65536D * Math.cos(i * 0.0030679614999999999D));
        }
    }

    // --- Memory / Loader Methods ---
    public static void nullLoader(){
        anIntArray1468 = null; anIntArray1470 = null; anIntArray1471 = null;
        anIntArray1472 = null; aBackgroundArray1474s = null; aBooleanArray1475 = null;
        anIntArray1476 = null; anIntArrayArray1478 = null; anIntArrayArray1479 = null;
        anIntArray1480 = null; anIntArray1482 = null; anIntArrayArray1483 = null;
    }

    public static void method364(){
        anIntArray1472 = new int[DrawingArea.height];
        for(int j=0;j<DrawingArea.height;j++) anIntArray1472[j] = DrawingArea.width*j;
        textureInt1 = DrawingArea.width/2; textureInt2 = DrawingArea.height/2;
    }

    public static void method365(int j,int k){
        anIntArray1472 = new int[k];
        for(int l=0;l<k;l++) anIntArray1472[l]=j*l;
        textureInt1=j/2; textureInt2=k/2;
    }

    public static void method366(){
        anIntArrayArray1478=null;
        for(int j=0;j<50;j++) anIntArrayArray1479[j]=null;
    }

    public static void method367(){
        if(anIntArrayArray1478==null){
            if(lowMem) anIntArrayArray1478=new int[anInt1477][16384];
            else       anIntArrayArray1478=new int[anInt1477][0x10000];
            for(int k=0;k<50;k++) anIntArrayArray1479[k]=null;
        }
    }

    public static void method368(StreamLoader streamLoader){
        anInt1473=0;
        for(int j=0;j<50;j++){
            try{
                aBackgroundArray1474s[j]=new Background(streamLoader,String.valueOf(j),0);
                if(lowMem && aBackgroundArray1474s[j].anInt1456==128)
                    aBackgroundArray1474s[j].downscale();
                else
                    aBackgroundArray1474s[j].expandToFullSize();
                anInt1473++;
            }catch(Exception e){}
        }
    }

    public static int method369(int i){
        if(anIntArray1476[i]!=0) return anIntArray1476[i];
        int r=0,g=0,b=0,len=anIntArrayArray1483[i].length;
        for(int k=0;k<len;k++){
            int c=anIntArrayArray1483[i][k];
            r+=(c>>16)&0xff; g+=(c>>8)&0xff; b+=c&0xff;
        }
        int avg=((r/len)<<16)|((g/len)<<8)|(b/len);
        avg=method373(avg,1.4D);
        if(avg==0) avg=1;
        return anIntArray1476[i]=avg;
    }

    public static void method370(int i){
        if(anIntArrayArray1479[i]==null) return;
        anIntArrayArray1478[anInt1477++]=anIntArrayArray1479[i];
        anIntArrayArray1479[i]=null;
    }
    
    public static void drawPerspectiveTriangle(
    	    int x0, int y0, float u0, float v0, float w0,
    	    int x1, int y1, float u1, float v1, float w1,
    	    int x2, int y2, float u2, float v2, float w2,
    	    int[] texturePixels
    	)

    	{
    	    int[] tex = texturePixels;
//
//    	    float uf0 = u0 / 65536f, uf1 = u1 / 65536f, uf2 = u2 / 65536f;
//    	    float vf0 = v0 / 65536f, vf1 = v1 / 65536f, vf2 = v2 / 65536f;
//    	    float wf0 = w0 / 65536f, wf1 = w1 / 65536f, wf2 = w2 / 65536f;

    	    drawPerspectiveTriangle(
    	    	    x0, y0, u0, v0, w0,
    	    	    x1, y1, u1, v1, w1,
    	    	    x2, y2, u2, v2, w2,
    	    	    texturePixels
    	    	);

    	}
    
    public static void drawPerspectiveTriangle(
    		int y0, int y1, int y2,
    	    int x0, int x1, int x2,
    	    int u0, int u1, int u2,
    	    int v0, int v1, int v2,
    	    int w0, int w1, int w2,
    	    int z0, int z1, int z2,
    	    int textureIndex

    	) {
    	    // paste your full perspective‐correct rasterizer here
    	    valsX = new int[]{x0, x1, x2};
    	    valsY = new int[]{y0, y1, y2};
    	    valsU = new int[]{(int)(u0 * 65536), (int)(u1 * 65536), (int)(u2 * 65536)};
    	    valsV = new int[]{(int)(v0 * 65536), (int)(v1 * 65536), (int)(v2 * 65536)};
    	    valsW = new int[]{(int)(w0 * 65536), (int)(w1 * 65536), (int)(w2 * 65536)};
    	    // the rest of your edge-walking and drawPerspectiveSpan loops
    	}



    private static int[] method371(int i){
        if(i==1) i=24;
        anIntArray1480[i]=anInt1481++;
        if(anIntArrayArray1479[i]!=null) return anIntArrayArray1479[i];
        int[] ai;
        if(anInt1477>0){
            ai=anIntArrayArray1478[--anInt1477];
            anIntArrayArray1478[anInt1477]=null;
        } else {
            int oldest=0,idx=-1;
            for(int l=0;l<anInt1473;l++){
                if(anIntArrayArray1479[l]!=null && (anIntArray1480[l]<oldest||idx<0)){
                    oldest=anIntArray1480[l]; idx=l;
                }
            }
            ai=anIntArrayArray1479[idx];
            anIntArrayArray1479[idx]=null;
        }
        anIntArrayArray1479[i]=ai;
        Background bg=aBackgroundArray1474s[i];
        int[] src=anIntArrayArray1483[i];
        if(lowMem){
            aBooleanArray1475[i]=false;
            for(int p=0;p<4096;p++){
                int c=ai[p]=src[bg.aByteArray1450[p]]&0xf8f8ff;
                if(c==0) aBooleanArray1475[i]=true;
                ai[4096+p]  = c-(c>>>3)&0xf8f8ff;
                ai[8192+p]  = c-(c>>>2)&0xf8f8ff;
                ai[12288+p] = c-(c>>>2)-(c>>>3)&0xf8f8ff;
            }
        } else {
            if(bg.anInt1452==64){
                for(int y=0;y<128;y++)
                    for(int x=0;x<128;x++)
                        ai[x+(y<<7)] = src[bg.aByteArray1450[(x>>1)+((y>>1)<<6)]];
            } else {
                for(int p=0;p<16384;p++) ai[p]=src[bg.aByteArray1450[p]];
            }
            aBooleanArray1475[i]=false;
            for(int p=0;p<16384;p++){
                int c=ai[p]&0xf8f8ff;
                if(c==0) aBooleanArray1475[i]=true;
                ai[16384+p] = c-(c>>>3)&0xf8f8ff;
                ai[32768+p] = c-(c>>>2)&0xf8f8ff;
                ai[49152+p] = c-(c>>>2)-(c>>>3)&0xf8f8ff;
            }
        }
        return ai;
    }

    public static void method372(double d){
        d+=Math.random()*0.03D-0.015D;
        int idx=0;
        for(int k=0;k<512;k++){
            double h=(k/8)/64D+0.0078125D, s=(k&7)/8D+0.0625D;
            for(int y=0;y<128;y++){
                double b=(double)y/128D, q, p, r=b, g=b, bl=b;
                if(s!=0){
                    q=b<0.5?b*(1+s):(b+s)-b*s;
                    p=b*2-q;
                    double[] t={h+0.3333,h,h-0.3333}, col=new double[3];
                    for(int c=0;c<3;c++){
                        if(t[c]<0) t[c]++; if(t[c]>1) t[c]--;
                        col[c]=t[c]*6<1? p+(q-p)*6*t[c]
                               :t[c]*2<1? q
                               :t[c]*3<2? p+(q-p)*(0.6666-t[c])*6
                               : p;
                    }
                    r=col[0]; g=col[1]; bl=col[2];
                }
                int ir=(int)(r*256D), ig=(int)(g*256D), ib=(int)(bl*256D),
                    rgb=(ir<<16)|(ig<<8)|ib;
                anIntArray1482[idx++]= (rgb=method373(rgb,d))==0?1:rgb;
            }
        }
        for(int i=0;i<50;i++){
            if(aBackgroundArray1474s[i]!=null){
                int[] src=aBackgroundArray1474s[i].anIntArray1451;
                anIntArrayArray1483[i]=new int[src.length];
                for(int p=0;p<src.length;p++){
                    int c=method373(src[p],d);
                    if((c&0xf8f8ff)==0 && p!=0) c=1;
                    anIntArrayArray1483[i][p]=c;
                }
            }
        }
        for(int i=0;i<50;i++) method370(i);
    }

    private static int method373(int c,double d){
        double r=Math.pow(((c>>16)&0xff)/256D,d),
               g=Math.pow(((c>>8)&0xff)/256D,d),
               b=Math.pow((c&0xff)/256D,d);
        return ((int)(r*256D)<<16)|((int)(g*256D)<<8)|(int)(b*256D);
    }

    // --- Utilities ---
    private static void swap(int[] a,int i,int j){int t=a[i];a[i]=a[j];a[j]=t;}
    private static void swapYUVW(int i,int j){
        swap(valsY,i,j); swap(valsX,i,j);
        swap(valsU,i,j); swap(valsV,i,j); swap(valsW,i,j);
    }
    private static void swapVertexData(int[] y,int[] x,int[] u,int[] v,int[] w,int i,int j){
        int t=y[i]; y[i]=y[j]; y[j]=t;
           t=x[i]; x[i]=x[j]; x[j]=t;
           t=u[i]; u[i]=u[j]; u[j]=t;
           t=v[i]; v[i]=v[j]; v[j]=t;
           t=w[i]; w[i]=w[j]; w[j]=t;
    }

    // --- Span Drawers ---
    private static void drawFlatSpan(int[] pixels,int off,int col,int xs,int xe){
        if(xs>=xe) return;
        if(aBoolean1462){
            xs= Math.max(xs,0);
            xe= Math.min(xe,DrawingArea.centerX);
        }
        int len=xe-xs, idx=off+xs;
        if(anInt1465==0){
            for(int i=0;i<len;i++) pixels[idx++]=col;
        } else {
            int alpha=anInt1465, inv=256-alpha;
            int bc=((col&0xff00ff)*inv>>8&0xff00ff)+((col&0xff00)*inv>>8&0xff00);
            for(int i=0;i<len;i++){
                int dst=pixels[idx];
                pixels[idx++]=bc
                  +(((dst&0xff00ff)*alpha>>8)&0xff00ff)
                  +(((dst&0xff00)*alpha>>8)&0xff00);
            }
        }
    }

    private static void drawAffineTexturedSpan(
    	    int[] pixels, int off,
    	    int xs, int xe,
    	    int us, int ue,
    	    int[] texture
    	) {
    	    if (xs >= xe) return;
    	    xs = Math.max(xs, 0);
    	    xe = Math.min(xe, DrawingArea.centerX);
    	    int len = xe - xs, du = len > 0 ? (ue - us) / len : 0, u = us, idx = off + xs;
    	    for (int i = 0; i < len; i++) {
    	        pixels[idx++] = texture[(u >> 8) & 0xFFFF];
    	        u += du;
    	    }
    	}



    public static void drawPerspectiveSpan(int[] pixels,int off,int xs,int xe,
        float us,float ue,float vs,float ve,float ws,float we,int[] tex){
        if(xs>=xe) return;
        xs=Math.max(xs,0); xe=Math.min(xe,DrawingArea.centerX);
        int len=xe-xs; if(len<=0) return;
        float du=(ue-us)/len, dv=(ve-vs)/len, dw=(we-ws)/len;
        float u=us,v=vs,w=ws; int idx=off+xs;
        for(int i=0;i<len;i++){
            float iw=1.0f/w;
            int tu=(int)(u*iw)&TEX_MASK, tv=(int)(v*iw)&TEX_MASK;
            pixels[idx++]=tex[tu+(tv<<TEX_SHIFT)];
            u+=du; v+=dv; w+=dw;
        }
    }

    public static void drawFlatTriangle(int i, int j, int k,
                                    int l, int i1, int j1,
                                    int k1) {
    // Compute slopes
    int l1 = 0;
    if (j != i) l1 = (i1 - l << 16) / (j - i);
    int i2 = 0;
    if (k != j) i2 = (j1 - i1 << 16) / (k - j);
    int j2 = 0;
    if (k != i) j2 = (l - j1 << 16) / (i - k);

    // Case 1: i is the topmost vertex
    if (i <= j && i <= k) {
        if (i >= DrawingArea.bottomY) return;
        if (j > DrawingArea.bottomY) j = DrawingArea.bottomY;
        if (k > DrawingArea.bottomY) k = DrawingArea.bottomY;

        // Split on whether the middle vertex j is left or right of k
        if (j < k) {
            // Prepare edge start values
            j1 = l <<= 16;
            if (i < 0) {
                j1 -= j2 * i;
                l  -= l1 * i;
                i   = 0;
            }
            i1 <<= 16;
            if (j < 0) {
                i1 -= i2 * j;
                j  = 0;
            }
            // Top half: scan from y=i to y=j
            int off = anIntArray1472[i];
            int count = j - i;
            while (--count >= 0) {
                drawFlatSpan(DrawingArea.pixels, off, k1, j1 >> 16, l >> 16);
                j1 += j2;
                l  += l1;
                off += DrawingArea.width;
            }
            // Bottom half: scan from y=j to y=k
            count = k - j;
            while (--count >= 0) {
                drawFlatSpan(DrawingArea.pixels, off, k1, j1 >> 16, i1 >> 16);
                j1 += j2;
                i1 += i2;
                off += DrawingArea.width;
            }
            return;
        } else {
            // j >= k: middle vertex is to the right of bottom vertex
            i1 = l <<= 16;
            if (i < 0) {
                i1 -= j2 * i;
                l  -= l1 * i;
                i   = 0;
            }
            j1 <<= 16;
            if (k < 0) {
                j1 -= i2 * k;
                k   = 0;
            }
            int off = anIntArray1472[i];
            // Decide which edge is on which side
            if ((i != k && j2 < l1) || (i == k && i2 > l1)) {
                // Left edge uses i→k, right edge uses i→j
                int count = k - i;
                while (--count >= 0) {
                    drawFlatSpan(DrawingArea.pixels, off, k1, i1 >> 16, l >> 16);
                    i1 += j2;
                    l  += l1;
                    off += DrawingArea.width;
                }
                count = j - k;
                while (--count >= 0) {
                    drawFlatSpan(DrawingArea.pixels, off, k1, j1 >> 16, l >> 16);
                    j1 += i2;
                    l  += l1;
                    off += DrawingArea.width;
                }
                return;
            } else {
                // Left edge uses i→j, right edge uses i→k
                int count = k - i;
                while (--count >= 0) {
                    drawFlatSpan(DrawingArea.pixels, off, k1, l >> 16, i1 >> 16);
                    i1 += j2;
                    l  += l1;
                    off += DrawingArea.width;
                }
                count = j - k;
                while (--count >= 0) {
                    drawFlatSpan(DrawingArea.pixels, off, k1, l >> 16, j1 >> 16);
                    j1 += i2;
                    l  += l1;
                    off += DrawingArea.width;
                }
                return;
            }
        }
    }

    // Case 2: j is the topmost vertex
    if (j <= k) {
        if (j >= DrawingArea.bottomY) return;
        if (k > DrawingArea.bottomY) k = DrawingArea.bottomY;
        if (i > DrawingArea.bottomY) i = DrawingArea.bottomY;

        if (k < i) {
            // Prepare edges: from j→k and j→i
            l = i1 <<= 16;
            if (j < 0) {
                l  -= l1 * j;
                i1 -= i2 * j;
                j  = 0;
            }
            j1 <<= 16;
            if (k < 0) {
                j1 -= j2 * k;
                k  = 0;
            }
            int off = anIntArray1472[j];
            if ((j != k && l1 < i2) || (j == k && l1 > j2)) {
                // Left edge j→i, right edge j→k
                int count = i - k;
                while (--count >= 0) {
                    drawFlatSpan(DrawingArea.pixels, off, k1, l >> 16, i1 >> 16);
                    l  += l1;
                    i1 += i2;
                    off += DrawingArea.width;
                }
                count = k - j;
                while (--count >= 0) {
                    drawFlatSpan(DrawingArea.pixels, off, k1, l >> 16, j1 >> 16);
                    l  += l1;
                    j1 += j2;
                    off += DrawingArea.width;
                }
                return;
            } else {
                // Left edge j→k, right edge j→i
                int count = i - k;
                while (--count >= 0) {
                    drawFlatSpan(DrawingArea.pixels, off, k1, i1 >> 16, l >> 16);
                    l  += l1;
                    i1 += i2;
                    off += DrawingArea.width;
                }
                count = k - j;
                while (--count >= 0) {
                    drawFlatSpan(DrawingArea.pixels, off, k1, j1 >> 16, l >> 16);
                    l  += l1;
                    j1 += j2;
                    off += DrawingArea.width;
                }
                return;
            }
        } else {
            // k >= i
            j1 = i1 <<= 16;
            if (j < 0) {
                j1 -= l1 * j;
                i1 -= i2 * j;
                j  = 0;
            }
            l <<= 16;
            if (i < 0) {
                l  -= j2 * i;
                i   = 0;
            }
            int off = anIntArray1472[j];
            if (l1 < i2) {
                // Left edge j→k, right edge j→i
                int count = k - i;
                while (--count >= 0) {
                    drawFlatSpan(DrawingArea.pixels, off, k1, j1 >> 16, i1 >> 16);
                    j1 += l1;
                    i1 += i2;
                    off += DrawingArea.width;
                }
                count = i - j;
                while (--count >= 0) {
                    drawFlatSpan(DrawingArea.pixels, off, k1, l >> 16, i1 >> 16);
                    l  += j2;
                    i1 += i2;
                    off += DrawingArea.width;
                }
                return;
            } else {
                // Left edge j→i, right edge j→k
                int count = k - i;
                while (--count >= 0) {
                    drawFlatSpan(DrawingArea.pixels, off, k1, i1 >> 16, j1 >> 16);
                    j1 += l1;
                    i1 += i2;
                    off += DrawingArea.width;
                }
                count = i - j;
                while (--count >= 0) {
                    drawFlatSpan(DrawingArea.pixels, off, k1, l >> 16, j1 >> 16);
                    l  += j2;
                    j1 += l1;
                    off += DrawingArea.width;
                }
                return;
            }
        }
    }

    // Case 3: k is the topmost vertex
    if (k >= DrawingArea.bottomY) return;
    if (i > DrawingArea.bottomY) i = DrawingArea.bottomY;
    if (j > DrawingArea.bottomY) j = DrawingArea.bottomY;

    if (i < j) {
        // Prepare edges: k→i and k→j
        i1 = j1 <<= 16;
        if (k < 0) {
            i1 -= i2 * k;
            j1 -= j2 * k;
            k  = 0;
        }
        l <<= 16;
        if (i < 0) {
            l  -= l1 * i;
            i   = 0;
        }
        int off = anIntArray1472[k];
        if (i2 < j2) {
            // Left edge k→j, right edge k→i
            int count = j - i;
            while (--count >= 0) {
                drawFlatSpan(DrawingArea.pixels, off, k1, i1 >> 16, j1 >> 16);
                i1 += i2;
                j1 += j2;
                off += DrawingArea.width;
            }
            count = i - k;
            while (--count >= 0) {
                drawFlatSpan(DrawingArea.pixels, off, k1, i1 >> 16, l >> 16);
                i1 += i2;
                l  += l1;
                off += DrawingArea.width;
            }
            return;
        } else {
            // Left edge k→i, right edge k→j
            int count = j - i;
            while (--count >= 0) {
                drawFlatSpan(DrawingArea.pixels, off, k1, j1 >> 16, i1 >> 16);
                i1 += i2;
                j1 += j2;
                off += DrawingArea.width;
            }
            count = i - k;
            while (--count >= 0) {
                drawFlatSpan(DrawingArea.pixels, off, k1, l >> 16, i1 >> 16);
                i1 += i2;
                l  += l1;
                off += DrawingArea.width;
            }
            return;
        }
    } else {
        // i >= j
        l = j1 <<= 16;
        if (k < 0) {
            l  -= i2 * k;
            j1 -= j2 * k;
            k   = 0;
        }
        i1 <<= 16;
        if (j < 0) {
            i1 -= l1 * j;
            j   = 0;
        }
        int off = anIntArray1472[k];
        if (i2 < j2) {
            // Left edge k→i, right edge k→j
            int count = i - j;
            while (--count >= 0) {
                drawFlatSpan(DrawingArea.pixels, off, k1, l >> 16, j1 >> 16);
                l  += i2;
                j1 += j2;
                off += DrawingArea.width;
            }
            count = j - k;
            while (--count >= 0) {
                drawFlatSpan(DrawingArea.pixels, off, k1, i1 >> 16, j1 >> 16);
                j1 += j2;
                i1 += l1;
                off += DrawingArea.width;
            }
        } else {
            // Left edge k→j, right edge k→i
            int count = i - j;
            while (--count >= 0) {
                drawFlatSpan(DrawingArea.pixels, off, k1, j1 >> 16, l >> 16);
                l  += i2;
                j1 += j2;
                off += DrawingArea.width;
            }
            count = j - k;
            while (--count >= 0) {
                drawFlatSpan(DrawingArea.pixels, off, k1, i1 >> 16, j1 >> 16);
                j1 += j2;
                i1 += l1;
                off += DrawingArea.width;
            	}
        	}
    	}
    }
    
    public static void drawAffineTexturedTriangle(
    	    int y0, int y1, int y2,
    	    int x0, int x1, int x2,
    	    int u0, int u1, int u2,
    	    int textureIndex
    	) {
    	    int[] tex = anIntArrayArray1483[textureIndex];
    	    aBoolean1463 = !aBooleanArray1475[textureIndex];

    	    int[] ys = { y0, y1, y2 };
    	    int[] xs = { x0, x1, x2 };
    	    int[] us = { u0, u1, u2 };

    	    // Dummy arrays for compatibility with swapVertexData
    	    int[] dummy = { 0, 0, 0 };

    	    swapVertexData(ys, xs, us, dummy, dummy, 0, 1);
    	    swapVertexData(ys, xs, us, dummy, dummy, 1, 2);
    	    swapVertexData(ys, xs, us, dummy, dummy, 0, 1);

    	    float dy02 = ys[2] - ys[0];
    	    if (dy02 == 0) return;

    	    float dy01 = ys[1] - ys[0];
    	    float dy12 = ys[2] - ys[1];

    	    float dx01 = dy01 != 0 ? (xs[1] - xs[0]) / dy01 : 0;
    	    float dx12 = dy12 != 0 ? (xs[2] - xs[1]) / dy12 : 0;
    	    float dx02 = (xs[2] - xs[0]) / dy02;

    	    float du01 = dy01 != 0 ? (us[1] - us[0]) / dy01 : 0;
    	    float du12 = dy12 != 0 ? (us[2] - us[1]) / dy12 : 0;
    	    float du02 = (us[2] - us[0]) / dy02;

    	    float xL = xs[0], xR = xs[0];
    	    float uL = us[0], uR = us[0];

    	    for (int y = ys[0]; y < ys[1]; y++) {
    	        drawAffineTexturedSpan(DrawingArea.pixels, anIntArray1472[y], (int) xL, (int) xR, (int) uL, (int) uR, tex);
    	        xL += dx01; uL += du01;
    	        xR += dx02; uR += du02;
    	    }

    	    xL = xs[1]; uL = us[1];

    	    for (int y = ys[1]; y < ys[2]; y++) {
    	        drawAffineTexturedSpan(DrawingArea.pixels, anIntArray1472[y], (int) xL, (int) xR, (int) uL, (int) uR, tex);
    	        xL += dx12; uL += du12;
    	        xR += dx02; uR += du02;
    	    }
    	}

    public static void rasterTriangle(
    		int[] tex,
    		int y0,int y1,int y2,
    		int x0,int x1,int x2,
    		int u0,int u1,int u2,
    		int dx0,int dx1,int dx2,
    		int du0,int du1,int du2
    		){
        	int xA=x0,xB=x0,uA=u0,uB=u0;
        	
        	for(int y=y0;y<y1;y++){
        		int xs=xA>>16, xe=xB>>16, us=uA>>7, ue=uB>>7;
        		drawAffineTexturedSpan(
        				DrawingArea.pixels,
        				anIntArray1472[y],
        				xs,xe,
        				us,ue,
        				tex);
	        		xA+=dx0; xB+=dx2; 
	        		uA+=du0; uB+=du2;
        }
        xA=x1; uA=u1;
        for(int y=y1;y<y2;y++){
            int xs=xA>>16, xe=xB>>16, us=uA>>7, ue=uB>>7;
            drawAffineTexturedSpan(DrawingArea.pixels,anIntArray1472[y],xs,xe,us,ue, tex);
            xA+=dx1; xB+=dx2; uA+=du1; uB+=du2;
        }
    }

    public static void rasterAffineTriangle(int[] tex,int y0,int y1,int y2,int x0,int x1,int x2,int u0,int u1,int u2,int dx0,int dx1,int dx2,int du0,int du1,int du2){
        if(y0>=DrawingArea.bottomY&&y1>=DrawingArea.bottomY&&y2>=DrawingArea.bottomY)return;
        if(y1<y0){int t=y0;y0=y1;y1=t; t=x0;x0=x1;x1=t; t=u0;u0=u1;u1=t;}
        if(y2<y0){int t=y0;y0=y2;y2=t; t=x0;x0=x2;x2=t; t=u0;u0=u2;u2=t;}
        if(y2<y1){int t=y1;y1=y2;y2=t; t=x1;x1=x2;x2=t; t=u1;u1=u2;u2=t;}
        int dy1=y1-y0, dy2=y2-y0; if(dy2==0)return;
        int dxA=(x1-x0)<<16, dxB=(x2-x0)<<16, duA=(u1-u0)<<7, duB=(u2-u0)<<7;
        dx0=dy1!=0?dxA/dy1:0; int dx2F=dxB/dy2;
        du0=dy1!=0?duA/dy1:0; int du2F=duB/dy2;
        rasterTriangle(null,y0,y1,y2,x0<<16,x1<<16,x2<<16,u0<<7,u1<<7,u2<<7,(int)dx0,0,dx2F,(int)du0,0,du2F);
    }
}