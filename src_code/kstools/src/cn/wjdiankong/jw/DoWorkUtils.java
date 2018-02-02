package cn.wjdiankong.jw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cn.wjdiankong.kstools.AnalysisApk;
import cn.wjdiankong.kstools.ApkSign;

public class DoWorkUtils {
	
	public static ArrayList<String> allDexList = new ArrayList<String>();
	public static ArrayList<String> errorDexList = new ArrayList<String>();
	
	/**
	 * ��ȡӦ��ǩ����Ϣ
	 * @param srcApkFile
	 * @return
	 */
	public static boolean getAppSign(File srcApkFile){
		try{
			long time = System.currentTimeMillis();
			System.out.println("��һ��==> ��ȡapk�ļ�ǩ����Ϣ");
			String sign = ApkSign.getApkSignInfo(srcApkFile.getAbsolutePath());
			Const.appSign = sign;
			System.out.println("signed:"+sign);
			System.out.println("��ȡapkǩ����Ϣ�ɹ�===��ʱ:"+((System.currentTimeMillis()-time)/1000)+"s\n\n");
			return true;
		}catch(Exception e){
			System.out.println("��ȡapkǩ����Ϣʧ�ܣ��˳���:"+e.toString());
			return false;
		}
	}
	
	/**
	 * ��ȡӦ�������
	 */
	public static boolean getAppEnter(File srcApkFile){
		try{
			long time = System.currentTimeMillis();
			System.out.println("�ڶ���==> ��ȡapk�ļ������Ϣ");
			String enter = AnalysisApk.getAppEnterApplication(srcApkFile.getAbsolutePath());
			Const.entryClassName = enter.replace(".", "/");
			System.out.println("Ӧ�������:"+enter);
			System.out.println("��ȡapk�������Ϣ�ɹ�===��ʱ:"+((System.currentTimeMillis()-time)/1000)+"s\n\n");
			return true;
		}catch(Exception e){
			System.out.println("��ȡapk�������Ϣʧ�ܣ��˳���:"+e.toString());
			FileUtils.printException(e);
			return false;
		}
	}
	
	/**
	 * ��ѹapk
	 */
	public static boolean zipApkWork(File srcApkFile, String unZipDir){
		try {
			long time = System.currentTimeMillis();
			System.out.println("������==> ��ѹapk�ļ�:"+srcApkFile.getAbsolutePath());
			FileUtils.decompressDexFile(srcApkFile.getAbsolutePath(), unZipDir);
			System.out.println("��ѹapk�ļ�����===��ʱ:"+((System.currentTimeMillis()-time)/1000)+"s\n\n");
			return true;
		} catch (Throwable e) {
			System.out.println("��ѹapk�ļ�ʧ�ܣ��˳���:"+e.toString());
			return false;
		}
	}
	
	/**
	 * ɾ��ǩ���ļ�
	 */
	public static boolean deleteMetaInf(String unZipDir, String aaptCmdDir, String srcApkPath){
		try{
			long time = System.currentTimeMillis();
			File metaFile = new File(unZipDir + Const.METAINFO);
			System.out.println("���Ĳ�==> ɾ��ǩ���ļ�");
			if(metaFile.exists()){
				File[] metaFileList = metaFile.listFiles();
				File aaptFile = new File(aaptCmdDir);
				String cmd = aaptFile.getAbsolutePath() + " remove " + new File(srcApkPath).getAbsolutePath();
				for(File f : metaFileList){
					cmd = cmd + " " + Const.METAINFO + f.getName();
				}
				System.out.println("ɾ��ǩ���ļ�����:"+cmd);
				execCmd(cmd, true);
			}
			System.out.println("ɾ��ǩ���ļ�����===��ʱ:"+((System.currentTimeMillis()-time)/1000)+"s\n\n");
			return true;
		}catch(Throwable e){
			System.out.println("ɾ��ǩ���ļ�ʧ�ܣ��˳���:"+e.toString());
			return false;
		}
	}
	
	/**
	 * ��dexת����smali
	 */
	public static boolean dexToSmali(String dexFile, String smaliDir){
		File smaliDirF = new File(smaliDir);
		if(smaliDirF.exists()){
			smaliDirF.delete();
		}
		smaliDirF.mkdirs();
		System.out.println("���岽==> ��dexת����smali");
		String javaCmd = "java -jar libs"+File.separator+"baksmali.jar -o "+smaliDir + " " + dexFile;
		long startTime = System.currentTimeMillis();
		try {
			Process pro = Runtime.getRuntime().exec(javaCmd);
			int status = pro.waitFor();
			if(status == 0){
				System.out.println("dexת��smali�ɹ�===��ʱ:"+((System.currentTimeMillis()-startTime)/1000)+"s\n\n");
				return true;
			}
			System.out.println("dexת��smaliʧ��,status:"+status);
			return false;
		} catch (Exception e) {
			System.out.println("dexת��smaliʧ��:"+e.toString());
			return false;
		}
	}
	
	/**
	 * �滻ԭʼǩ���Ͱ���
	 */
	public static boolean setSignAndPkgName(){
		System.out.println("������==> �������滻ԭʼǩ���Ͱ�����Ϣ");
		File pmsSmaliDirF = new File(JWMain.rootPath + Const.smaliTmpDir + File.separator + Const.pmsSmaliDir);
		if(!pmsSmaliDirF.exists()){
			pmsSmaliDirF.mkdirs();
		}
		FileReader reader = null;
        BufferedReader br = null;
        FileWriter writer = null;
		try{
			long startTime = System.currentTimeMillis();
			FileUtils.fileCopy(JWMain.rootPath+File.separator+Const.smaliFileHandler, pmsSmaliDirF.getAbsolutePath() + File.separator + Const.smaliFileHandler);
			writer = new FileWriter(pmsSmaliDirF.getAbsolutePath() + File.separator + Const.smaliFilePMS);
			reader = new FileReader(JWMain.rootPath+File.separator+Const.smaliFilePMS);
            br = new BufferedReader(reader);
            String str = null;
            while((str = br.readLine()) != null) {
            	if(str.contains(Const.signLineTag)){
            		writer.write(str+"\n");
            		String signStr = "\tconst-string v0, \"" + Const.appSign + "\"";
            		writer.write(signStr+"\n");
            		br.readLine();
            	}if(str.contains(Const.pkgNameLineTag)){
            		String pkgNameStr = "\tconst-string v1, \"" + Const.appPkgName + "\"";
            		writer.write(pkgNameStr+"\n");
            		br.readLine();
            	}else{
            		writer.write(str+"\n");
            	}
            }
            System.out.println("����ǩ���Ͱ����ɹ�===��ʱ:"+((System.currentTimeMillis()-startTime)/1000)+"s\n\n");
			return true;
		}catch(Exception e){
			System.out.println("����ǩ���Ͱ���ʧ��:"+e.toString());
		}finally{
			if(reader != null){
				try{
					reader.close();
				}catch(Exception e){
				}
			}
			if(br != null){
				try{
					br.close();
				}catch(Exception e){
				}
			}
			if(writer != null){
				try{
					writer.close();
				}catch(Exception e){
				}
			}
		}
		return false;
	}
	
	/**
	 * ����hook����
	 */
	public static boolean insertHookCode(){
		System.out.println("���߲�==> ���hook����");
		long startTime = System.currentTimeMillis();
		String enterFile = JWMain.rootPath + Const.smaliTmpDir + File.separator + Const.entryClassName.replace(".", File.separator) + ".smali";
		String enterFileTmp = JWMain.rootPath + Const.smaliTmpDir + File.separator + Const.entryClassName.replace(".", File.separator) + "_tmp.smali";
		FileReader reader = null;
        BufferedReader br = null;
        FileWriter writer = null;
        boolean isWorkSucc = false;
        try{
        	reader = new FileReader(enterFile);
        	br = new BufferedReader(reader);
        	writer = new FileWriter(enterFileTmp);
            String str = null;
            boolean isSucc = false;
            int isEntryMethod = -1;
            while((str = br.readLine()) != null) {
            	if(isSucc){
            		writer.write(str+"\n");
            		continue;
            	}
            	if(Const.isApplicationEntry){
            		if(str.contains(Const.applicationAttachLineTag)){
            			isEntryMethod = 0;
            		}else if(str.contains(Const.applicationCreateLineTag)){
            			isEntryMethod = 1;
            		}
            	}else{
            		if(str.contains(Const.activityCreateLineTag)){
            			isEntryMethod = 2;
            		}
            	}
            	if(str.contains(Const.methodEndStr)){
            		isEntryMethod = -1;
            	}
            	
            	writer.write(str+"\n");
            	
            	if(isEntryMethod == 0){
            		writer.write(Const.hookAttachCodeStr);
            		isSucc = true;
            	}else if(isEntryMethod == 1){
            		writer.write(Const.hookCreateCodeStr);
            		isSucc = true;
            	}else if(isEntryMethod == 2){
            		writer.write(Const.hookCreateCodeStr);
            		isSucc = true;
            	}
            }
            System.out.println("����hook����ɹ�===��ʱ"+((System.currentTimeMillis()-startTime)/1000)+"s\n\n");
            isWorkSucc = true;
        }catch(Exception e){
        	System.out.println("����hook����ʧ��:"+e.toString());
        }finally{
        	if(reader != null){
				try{
					reader.close();
				}catch(Exception e){
				}
			}
			if(br != null){
				try{
					br.close();
				}catch(Exception e){
				}
			}
			if(writer != null){
				try{
					writer.close();
				}catch(Exception e){
				}
			}
        }
        
        File entryFile = new File(enterFile);
        entryFile.delete();
        File entryFileTmp = new File(enterFileTmp);
        entryFileTmp.renameTo(new File(enterFile));
        
		return isWorkSucc;
	}
	
	/**
	 * ��smaliת����dex
	 */
	public static boolean smaliToDex(String smaliDir, String dexFile){
		System.out.println("�ڰ˲�==> ��smaliת����dex");
		File dexFileF = new File(dexFile);
		if(dexFileF.exists()){
			dexFileF.delete();
		}
		String javaCmd = "java -jar libs"+File.separator+"smali.jar "+smaliDir + " -o " + dexFile;
		long startTime = System.currentTimeMillis();
		try {
			Process pro = Runtime.getRuntime().exec(javaCmd);
			int status = pro.waitFor();
			if(status == 0){
				System.out.println("smaliת��dex�ɹ�===��ʱ:"+((System.currentTimeMillis()-startTime)/1000)+"s\n\n");
				return true;
			}
			System.out.println("smaliת��dexʧ��,status:"+status);
			return false;
		} catch (Exception e) {
			System.out.println("smaliת��dexʧ��:"+e.toString());
			return false;
		}
	}
	
	
	/**
	 * ʹ��aapt�������dex�ļ���apk��
	 */
	public static boolean addDexToApk(String aaptCmdDir, String unZipDir, String srcApkPath){
		try{
			System.out.println("�ھŲ�==> ��dex�ļ���ӵ�Դapk��");
			long time = System.currentTimeMillis();
			File aaptFile = new File(aaptCmdDir);
			String cmd = aaptFile.getAbsolutePath() + " remove " + new File(srcApkPath).getAbsolutePath();
			File classDir = new File(unZipDir);
			File[] classListFile = classDir.listFiles();
			for(File file : classListFile){
				if(file.getName().endsWith("classes.dex")){
					cmd = cmd + " " + file.getName();
				}
			}
			System.out.println("cmd:"+cmd);
			if(!execCmd(cmd, true)){
				System.out.println("���dex�ļ���apk��ʧ�ܣ��˳���");
				return false;
			}

			String addCmd = aaptFile.getAbsolutePath() + " add " + new File(srcApkPath).getAbsolutePath();
			for(File file : classListFile){
				if(file.getName().endsWith(".dex")){
					addCmd = addCmd + " " + file.getName();
				}
			}
			System.out.println("cmd:"+addCmd);
			if(!execCmd(addCmd, true)){
				System.out.println("���dex�ļ���apk��ʧ�ܣ��˳���");
				return false;
			}
			System.out.println("���dex�ļ���apk�н���===��ʱ:"+((System.currentTimeMillis()-time)/1000)+"s\n\n");
			return true;
		}catch(Throwable e){
			System.out.println("���dex�ļ���apk��ʧ�ܣ��˳���:"+e.toString());
			return false;
		}
	}
	
	/**
	 * ǩ��apk�ļ�
	 */
	public static boolean signApk(String srcApkPath, String rootPath){
		try{
			System.out.println("��ʮ��==> ��ʼǩ��apk�ļ�:"+srcApkPath);
			long time = System.currentTimeMillis();
			String keystore = "cyy_game.keystore";
			File signFile = new File(rootPath+File.separator+keystore);
			if(!signFile.exists()){
				System.out.println("ǩ���ļ�:"+signFile.getAbsolutePath()+" �����ڣ���Ҫ�Լ��ֶ�ǩ��");
				return false;
			}
			String storePass = "cyy1888";
			StringBuilder signCmd = new StringBuilder("jarsigner");
			signCmd.append(" -verbose -keystore ");
			signCmd.append(keystore);
			signCmd.append(" -storepass ");
			signCmd.append(storePass);
			signCmd.append(" -signedjar ");
			signCmd.append("signed.apk ");
			signCmd.append(srcApkPath + " ");
			signCmd.append(keystore + " ");
			signCmd.append("-digestalg SHA1 -sigalg MD5withRSA");
			execCmd(signCmd.toString(), false);
			System.out.println("ǩ��apk�ļ�����===��ʱ:"+((System.currentTimeMillis()-time)/1000)+"s\n\n");
			return true;
		}catch(Throwable e){
			System.out.println("����ǩ��apk�ļ�ʧ�ܣ��˳���:"+e.toString());
			return false;
		}
	}
	
	/**
	 * ����ɾ������
	 */
	public static void deleteTmpFile(String rootPath){
		//ɾ����ѹ֮���Ŀ¼
		FileUtils.deleteDirectory(rootPath+Const.unZipDir);
		//ɾ��smaliĿ¼
		FileUtils.deleteDirectory(rootPath+Const.smaliTmpDir);
		//ɾ����ʱdex�ļ�
		FileUtils.deleteFile(rootPath+File.separator+"classes.dex");
	}
	
	/**
	 * ִ������
	 * @param cmd
	 * @param isOutputLog
	 * @return
	 */
	public static boolean execCmd(String cmd, boolean isOutputLog){
		BufferedReader br = null;
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				if(isOutputLog)
					System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println("cmd error:"+e.toString());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

}
