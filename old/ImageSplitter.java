package com.minephone.network;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

/**
 * 图片分割工具
 * 
 * @author ping
 * @create 2014-8-31 下午4:10:58
 */
public class ImageSplitter {

	/**
	 * 分割
	 * 
	 * @author ping
	 * @create 2014-8-31 下午7:12:16
	 * @param bitmap 原图片
	 * @param xcount 分割横向的数量
	 * @param ycount 分割纵向的数量
	 * @return
	 */
	public static List<ImagePiece> split(Bitmap bitmap, int xcount, int ycount) {

		List<ImagePiece> pieces = new ArrayList<ImagePiece>(xcount * ycount);
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int pieceWidth = width / xcount;
		int pieceHeight = height / ycount;
		for (int i = 0; i < xcount; i++) {
			for (int j = 0; j < ycount; j++) {
				ImagePiece piece = new ImagePiece();
				piece.xindex = i;
				piece.yindex = j;
				int xValue = i * pieceWidth;
				int yValue = j * pieceHeight;
				piece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue,
						pieceWidth, pieceHeight);
				pieces.add(piece);
			}
		}

		return pieces;
	}

}
