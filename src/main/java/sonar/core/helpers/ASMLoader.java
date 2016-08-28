package sonar.core.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import sonar.core.SonarCore;
import sonar.core.utils.Pair;

public class ASMLoader {

	public enum ASMLog {
		LOADED, ERROR, MODID;
	}

	public static void log(ASMLog log, Class type, ASMData asm, String modid) {
		switch (log) {
		case ERROR:
			SonarCore.logger.error(type.getSimpleName() + " couldn't be loaded: {}", asm.getClassName());
			break;
		case LOADED:
			SonarCore.logger.info(type.getSimpleName() + " loaded successfully: {}", asm.getClassName());
			break;
		case MODID:
			SonarCore.logger.error(String.format("Couldn't load" + type.getSimpleName() + "%s for modid %s", asm.getClassName(), modid));
			break;
		default:
			break;
		}
	}

	public static <T> List<T> getInstances(@Nonnull ASMDataTable asmDataTable, Class annotation, Class<T> instanceClass, boolean checkModid) {
		String annotationClassName = annotation.getCanonicalName();
		Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
		List<T> instances = new ArrayList<>();
		for (ASMDataTable.ASMData asmData : asmDatas) {
			String modid = checkModid ? (String) asmData.getAnnotationInfo().get("modid") : "";
			if (!checkModid || Loader.isModLoaded(modid)) {
				try {
					Class<?> asmClass = Class.forName(asmData.getClassName());
					Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
					T instance = asmInstanceClass.newInstance();
					instances.add(instance);
					log(ASMLog.LOADED, instanceClass, asmData, modid);
					continue;
				} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
					log(ASMLog.ERROR, instanceClass, asmData, modid);
					continue;
				}
			} else {
				log(ASMLog.MODID, instanceClass, asmData, modid);
			}
		}
		return instances;
	}

	public static <T> List<Pair<ASMDataTable.ASMData, Class<? extends T>>> getClasses(@Nonnull ASMDataTable asmDataTable, Class annotation, Class<T> instanceClass, boolean checkModid) {
		String annotationClassName = annotation.getCanonicalName();
		Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
		List<Pair<ASMDataTable.ASMData, Class<? extends T>>> classes = new ArrayList<>();
		for (ASMDataTable.ASMData asmData : asmDatas) {
			String modid = checkModid ? (String) asmData.getAnnotationInfo().get("modid") : "";
			if (!checkModid || Loader.isModLoaded(modid)) {
				try {
					Class<?> asmClass = Class.forName(asmData.getClassName());
					Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
					classes.add(new Pair(asmData, asmInstanceClass));
					log(ASMLog.LOADED, instanceClass, asmData, modid);
					continue;
				} catch (ClassNotFoundException e) {
					log(ASMLog.ERROR, instanceClass, asmData, modid);
					continue;
				}
			} else {
				log(ASMLog.MODID, instanceClass, asmData, modid);
			}
		}
		return classes;
	}
}
