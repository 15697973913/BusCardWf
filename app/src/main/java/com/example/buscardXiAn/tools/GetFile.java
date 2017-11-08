package com.example.buscardXiAn.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GetFile {
	public static List<String> lstFile = new ArrayList<String>(); // 结果 List
	private static boolean boo = true;
	private static String filename;

	public static List<String> GetFiles(String Path, String Extension,
										boolean IsIterative) // 搜索目录，扩展名，是否进入子文件夹
	{
		File[] files = new File(Path).listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile()) {
				if (f.getPath()
						.substring(f.getPath().length() - Extension.length())
						.equals(Extension)) // 判断扩展名
				{
					filename = getFileName(f.getPath());
					if (lstFile.size() == 0) {
						lstFile.add(f.getPath());
					} else {
						for (int j = 0; j < lstFile.size(); j++) {
							if (getFileName(lstFile.get(j)).equals(filename)) {
								boo = false;
							}
						}
						if (boo) {
							lstFile.add(f.getPath());
						}
					}
					if (!IsIterative)
						break;
				}
			} else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) // 忽略点文件（隐藏文件/文件夹）
				GetFiles(f.getPath(), Extension, IsIterative);
		}
		return lstFile;
	}

	// 截取文件名
	public static String getFileName(String pathandname) {

		int start = pathandname.lastIndexOf("/");
		if (start != -1) {
			return pathandname.substring(start + 1);
		} else {
			return null;
		}

	}

	// 截取后缀名
	public static String getHouzuiName(String pathandname) {
		// 获取最后一个点的位置
		int start = pathandname.lastIndexOf(".");
		if (start != -1) {
			// 从.前面一位开始截取
			return pathandname.substring(start + 1);
		} else {
			return null;
		}

	}

	/**
	 * 获取storage目录下的所有文件
	 */
	public static List<String> getstoragefilelis() {
		List<String> list = new ArrayList<String>();
		String path = "/storage";
		File[] file = new File(path).listFiles();
		if (file==null||file.length==0){
			return list;
		}
		for (int i = 0; i < file.length; i++) {
			list.add(file[i].getPath());
		}
		return list;
	}
}
