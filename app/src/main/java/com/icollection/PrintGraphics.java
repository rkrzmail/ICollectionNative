package com.icollection;
/*     */ 
/*     */ import android.graphics.Bitmap;
/*     */ import android.graphics.Bitmap.CompressFormat;
/*     */ import android.graphics.Bitmap.Config;
/*     */ import android.graphics.BitmapFactory;
/*     */ import android.graphics.Canvas;
/*     */ import android.graphics.Paint;
/*     */ import android.graphics.Paint.Style;
/*     */ import android.graphics.RectF;
/*     */ import android.graphics.Typeface;

/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
import java.io.InputStream;
/*     */ import java.io.PrintStream;

import com.sdk.bluetooth.android.FontProperty;
/*     */ 
/*     */ public class PrintGraphics
/*     */ {
/*  27 */   public Canvas canvas = null;
/*     */ 
/*  29 */   public Paint paint = null;
/*     */ 
/*  31 */   public Bitmap bm = null;
/*     */   public int width;
/*  35 */   public float length = 0.0F;
/*     */ 
/*  37 */   public byte[] bitbuf = null;
/*     */ 
/*     */   public int getLength()
/*     */   {
/*  43 */     return (int)this.length + 20;
/*     */   }
/*     */ 
/*     */   public void initCanvas(int w)
/*     */   {
/*  60 */     int h = 10 * w;
/*     */ 
/*  63 */     this.bm = Bitmap.createBitmap(w, h, Config.ARGB_4444);
/*  64 */     this.canvas = new Canvas(this.bm);
/*     */ 
/*  66 */     this.canvas.drawColor(-1);
/*  67 */     this.width = w;
/*  68 */     this.bitbuf = new byte[this.width / 8];
/*     */   }
/*     */ 
/*     */   public void initPaint()
/*     */   {
/*  80 */     this.paint = new Paint();
/*     */ 
/*  82 */     this.paint.setAntiAlias(true);
/*     */ 
/*  84 */     this.paint.setColor(-16777216);
/*     */ 
/*  86 */     this.paint.setStyle(Style.STROKE);
/*     */   }
/*     */ 
/*     */   
/*     */ 
/*     */   public void setLineWidth(float w)
/*     */   {
/* 156 */     this.paint.setStrokeWidth(w);
/*     */   }
/*     */ 
/*     */   public void drawText(float x, float y, String nStr)
/*     */   {
/* 163 */     this.canvas.drawText(nStr, x, y, this.paint);
/*     */ 
/* 165 */     if (this.length < y)
/* 166 */       this.length = y;
/*     */   }
/*     */ 
/*     */   public void drawLine(float x1, float y1, float x2, float y2)
/*     */   {
/* 174 */     this.canvas.drawLine(x1, y1, x2, y2, this.paint);
/* 175 */     float max = 0.0F;
/* 176 */     max = y1 > y2 ? y1 : y2;
/* 177 */     if (this.length < max)
/* 178 */       this.length = max;
/*     */   }
/*     */ 
/*     */   public void drawRectangle(float x1, float y1, float x2, float y2)
/*     */   {
/* 186 */     this.canvas.drawRect(x1, y1, x2, y2, this.paint);
/* 187 */     float max = 0.0F;
/* 188 */     max = y1 > y2 ? y1 : y2;
/* 189 */     if (this.length < max)
/* 190 */       this.length = max;
/*     */   }
/*     */ 
/*     */   public void drawEllips(float x1, float y1, float x2, float y2)
/*     */   {
/* 198 */     RectF re = new RectF(x1, y1, x2, y2);
/* 199 */     this.canvas.drawOval(re, this.paint);
/* 200 */     float max = 0.0F;
/* 201 */     max = y1 > y2 ? y1 : y2;
/* 202 */     if (this.length < max)
/* 203 */       this.length = max;
/*     */   }
/*     */  public void drawImage(float x, float y, Bitmap btm )
/*     */   {
/*     */     try
/*     */     {
/* 212 */        
/* 213 */       this.canvas.drawBitmap(btm, x, y, null);
/* 214 */       if (this.length < y + btm.getHeight())
/* 215 */         this.length = (y + btm.getHeight());
/*     */     }
/*     */     catch (Exception e) {
/* 218 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   public void drawImage(float x, float y, InputStream is)
/*     */   {
/*     */     try
/*     */     {
/* 212 */       Bitmap btm = BitmapFactory.decodeStream(is);
/* 213 */       this.canvas.drawBitmap(btm, x, y, null);
/* 214 */       if (this.length < y + btm.getHeight())
/* 215 */         this.length = (y + btm.getHeight());
/*     */     }
/*     */     catch (Exception e) {
/* 218 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */  public void drawImage(float x, float y, String path)
/*     */   {
/*     */     try
/*     */     {
/* 212 */       Bitmap btm = BitmapFactory.decodeFile(path);
/* 213 */       this.canvas.drawBitmap(btm, x, y, null);
/* 214 */       if (this.length < y + btm.getHeight())
/* 215 */         this.length = (y + btm.getHeight());
/*     */     }
/*     */     catch (Exception e) {
/* 218 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void printPng()
/*     */   {
/* 228 */     File f = new File("/sdcard/0.png");
/* 229 */     FileOutputStream fos = null;
/* 230 */     Bitmap nbm = Bitmap.createBitmap(this.bm, 0, 0, this.width, 
/* 231 */       getLength());
/*     */     try {
/* 233 */       fos = new FileOutputStream(f);
/* 234 */       nbm.compress(CompressFormat.PNG, 50, fos);
/*     */     } catch (FileNotFoundException e) {
/* 236 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] printDraw()
/*     */   {
/* 253 */     Bitmap nbm = Bitmap.createBitmap(this.bm, 0, 0, this.width, 
/* 254 */       getLength());
/*     */ 
/* 256 */     byte[] imgbuf = new byte[(this.width / 8 + 4) * getLength()];
/*     */ 
/* 258 */     int s = 0;
/*     */ 
/* 260 */     System.out.println("+++++++++++++++ Total Bytes: " + 
/* 261 */       (this.width / 8 + 4) * getLength());
/*     */ 
/* 264 */     for (int i = 0; i < getLength(); i++) {
/* 265 */       for (int k = 0; k < this.width / 8; k++)
/*     */       {
/* 267 */         int c0 = nbm.getPixel(k * 8 + 0, i);
/*     */         int p0;
/* 270 */         if (c0 == -1)
/* 271 */           p0 = 0;
/*     */         else {
/* 273 */           p0 = 1;
/*     */         }
/* 275 */         int c1 = nbm.getPixel(k * 8 + 1, i);
/*     */         int p1;
/* 276 */         if (c1 == -1)
/* 277 */           p1 = 0;
/*     */         else {
/* 279 */           p1 = 1;
/*     */         }
/* 281 */         int c2 = nbm.getPixel(k * 8 + 2, i);
/*     */         int p2;
/* 282 */         if (c2 == -1)
/* 283 */           p2 = 0;
/*     */         else {
/* 285 */           p2 = 1;
/*     */         }
/* 287 */         int c3 = nbm.getPixel(k * 8 + 3, i);
/*     */         int p3;
/* 288 */         if (c3 == -1)
/* 289 */           p3 = 0;
/*     */         else {
/* 291 */           p3 = 1;
/*     */         }
/* 293 */         int c4 = nbm.getPixel(k * 8 + 4, i);
/*     */         int p4;
/* 294 */         if (c4 == -1)
/* 295 */           p4 = 0;
/*     */         else {
/* 297 */           p4 = 1;
/*     */         }
/* 299 */         int c5 = nbm.getPixel(k * 8 + 5, i);
/*     */         int p5;
/* 300 */         if (c5 == -1)
/* 301 */           p5 = 0;
/*     */         else {
/* 303 */           p5 = 1;
/*     */         }
/* 305 */         int c6 = nbm.getPixel(k * 8 + 6, i);
/*     */         int p6;
/* 306 */         if (c6 == -1)
/* 307 */           p6 = 0;
/*     */         else {
/* 309 */           p6 = 1;
/*     */         }
/* 311 */         int c7 = nbm.getPixel(k * 8 + 7, i);
/*     */         int p7;
/* 312 */         if (c7 == -1)
/* 313 */           p7 = 0;
/*     */         else {
/* 315 */           p7 = 1;
/*     */         }
/* 317 */         int value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8 + 
/* 318 */           p5 * 4 + p6 * 2 + p7;
/* 319 */         this.bitbuf[k] = ((byte)value);
/*     */       }
/*     */ 
/* 324 */       if (i != 0) {
/* 325 */         s++;
/*     */       }
/* 327 */       imgbuf[s] = 22;
/* 328 */       s++;
/* 329 */       imgbuf[s] = ((byte)(this.width / 8));
/* 330 */       for (int t = 0; t < this.width / 8; t++) {
/* 331 */         s++;
/* 332 */         imgbuf[s] = this.bitbuf[t];
/*     */       }
/* 334 */       s++;
/* 335 */       imgbuf[s] = 21;
/* 336 */       s++;
/* 337 */       imgbuf[s] = 1;
/*     */     }
/*     */ 
/* 342 */     return imgbuf;
/*     */   }
/*     */ }

/* Location:           /Users/dewabrata/Documents/bluetooth printer/sprt/AndroidSDK&Demo_En20120522/SDK/AndroidSDK/
 * Qualified Name:     com.sdk.bluetooth.android.PrintGraphics
 * JD-Core Version:    0.6.2
 */