package com.example.view;

import java.util.Random;

import com.example.ffff.DensityUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * 
 * @author ping
 * @create 2014-11-4 上午10:15:03
 */
public class CodeView extends ImageView {
	private Context mcontext;
	private int mwidth;
	private int mheigth;
	private Bitmap mbitmap;
	
	public CodeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public CodeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CodeView(Context context) {
		super(context);
		init(context);
	}
	
	private void init (Context context) {
		mcontext = context;
	}
	
	 @Override
	protected void onDraw(Canvas canvas) {
			Log.i("test", "onDraw" +mwidth + "  mheigth" +mheigth );
		 if (mheigth == 0 || mwidth ==0  || mbitmap == null){
			 super.onDraw(canvas);			 
		 } else {
				Log.i("test", "drawBitmap:" +mbitmap);
			 canvas.drawBitmap(mbitmap, new Matrix(), new Paint());
//			 canvas.restore();
		 }
	}
	 
	 public void refeshCode(){
	        mbitmap = createBitmap();
	        postInvalidate();
	 }
	 
	 
	 
	 
	 
	 
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mwidth = DensityUtil.dip2px(mcontext, getMeasuredWidth()) ;
		mheigth =  DensityUtil.dip2px(mcontext, getMeasuredHeight()) ;
//		mwidth =  getMeasuredWidth() ;
//		mheigth =  getMeasuredHeight() ;
//		mwidth = 100;
//		mheigth = 50;
		base_padding_left = (int)(mwidth / 16);
		range_padding_left = base_padding_left;
//		base_padding_top = (int)(mheigth / 16);
//		range_padding_top  = base_padding_top;
		base_padding_top = 20;
		range_padding_top = 20;
		font_size = (int)(mheigth / 2.5);
		
		Log.i("test", "onMeasure:mwidth" +mwidth + "  mheigth" + heightMeasureSpec );
	}

	

	private static final char[] CHARS = {  
	        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
	        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',   
	        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',  
	        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',   
	        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'  
	    };  
	 
//	    private static final int BASE_PADDING_LEFT = 25, 
//	    		RANGE_PADDING_LEFT =30, BASE_PADDING_TOP =30, 
//	    		RANGE_PADDING_TOP = 25;  
//	    private static final int DEFAULT_WIDTH = 300, DEFAULT_HEIGHT =80; 
//	    //settings decided by the layout xml  
//	    //canvas width and height  
//	    private int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;   
	      
	    //random word space and pading_top  
	    private int base_padding_left = 0, 
	    		range_padding_left = 0,   
	            base_padding_top = 0,
	            range_padding_top = 0;  
	      
	    //number of chars, lines; font size  
	    private int codeLength = 6, 
	    		line_number = 2, 
	    		font_size = 20;  
	      
	    //variables  
	    private String code;  
	    private int padding_left, padding_top;  
	    private Random random = new Random();  
	      
	    /**
	     * 获取验证码
	     * @author ping
	     * @create 2014-11-4 上午10:34:13
	     * @return
	     */
	    private Bitmap createBitmap() {  
	        padding_left = 0;  
	          
	        Bitmap bp = Bitmap.createBitmap(mwidth, mheigth, Config.ARGB_8888);   
	        Canvas c = new Canvas(bp);  
	  
	        code = createCode();  
	          
	        c.drawColor(Color.WHITE);  
	        Paint paint = new Paint();  
	        paint.setTextSize(font_size);  
	          
	        for (int i = 0; i < code.length(); i++) {  
	            randomTextStyle(paint);  
	            randomPadding();  
	            c.drawText(code.charAt(i) + "", padding_left, padding_top, paint);  
	        }  
	  
	        for (int i = 0; i < line_number; i++) {  
	            drawLine(c, paint);  
	        }  
	        c.save( Canvas.ALL_SAVE_FLAG );//保存
	        c.restore();//  
	        
	        return bp;  
	    }  
	    
	    public String getCode() { 
	        return code;  
	    }    
	   
	      
	    private String createCode() {  
	        StringBuilder buffer = new StringBuilder();  
	        for (int i = 0; i < codeLength; i++) {  
	            buffer.append(CHARS[random.nextInt(CHARS.length)]);  
	        }  
	        return buffer.toString();  
	    }  
	      
	    private void drawLine(Canvas canvas, Paint paint) {  
	        int color = randomColor();  
	        int startX = random.nextInt(mwidth -5);  
	        int startY = random.nextInt(mheigth -5 );  
	        int stopX = random.nextInt(mwidth -10);  
	        int stopY = random.nextInt(mheigth -10);  
	        paint.setStrokeWidth(1);  
	        paint.setColor(color);  
	        canvas.drawLine(startX, startY, stopX, stopY, paint);  
	    }  
	      
	    private int randomColor() {  
	        return randomColor(1);  
	    }  
	  
	    private int randomColor(int rate) {  
	        int red = random.nextInt(256) / rate;  
	        int green = random.nextInt(256) / rate;  
	        int blue = random.nextInt(256) / rate;  
	        return Color.rgb(red, green, blue);  
	        
	    }  
	      
	    private void randomTextStyle(Paint paint) {  
	        int color = randomColor();  
	        paint.setColor(color);  
	        //paint.setFakeBoldText(random.nextBoolean());  //true为粗体，false为非粗体  
	        paint.setFakeBoldText(true);
	        float skewX = random.nextInt(11) / 10;  
	        skewX = random.nextBoolean() ? skewX : -skewX;  
	        paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，整数左斜  
//	      paint.setUnderlineText(true); //true为下划线，false为非下划线  
//	      paint.setStrikeThruText(true); //true为删除线，false为非删除线  
	    } 
	    
	      
	    private void randomPadding() {  
	        padding_left += base_padding_left + random.nextInt(range_padding_left);  
	        padding_top = base_padding_top + random.nextInt(range_padding_top);  
	    }  
}
