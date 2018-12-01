package sonar.core.helpers;

import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import org.apache.logging.log4j.Logger;
import sonar.core.SonarCore;
import sonar.core.api.asm.*;
import sonar.core.api.energy.IItemEnergyHandler;
import sonar.core.api.energy.ITileEnergyHandler;
import sonar.core.api.fluids.ISonarFluidHandler;
import sonar.core.api.planting.ISonarFertiliser;
import sonar.core.api.planting.ISonarHarvester;
import sonar.core.api.planting.ISonarPlanter;
import sonar.core.handlers.planting.PlantingHandler;
import sonar.core.utils.Pair;
import sonar.core.utils.SortingDirection;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ASMLoader {

    public static Comparator<ASMData> SORT_PRIORITY = (str1, str2) -> SonarHelper.compareWithDirection((int) str1.getAnnotationInfo().get("priority"), (int) str2.getAnnotationInfo().get("priority"), SortingDirection.UP);

	public enum ASMLog {
        LOADED, ERROR, MODID
	}

	public static void load(@Nonnull ASMDataTable asmDataTable) {
		SonarCore.tileEnergyHandlers = getTileEnergyHandlers(SonarCore.logger, asmDataTable);
		SonarCore.fluidHandlers = getFluidHandlers(SonarCore.logger, asmDataTable);
		SonarCore.itemEnergyHandlers = getItemEnergyHandlers(SonarCore.logger, asmDataTable);
		PlantingHandler.instance().planters = getPlanters(SonarCore.logger, asmDataTable);
		PlantingHandler.instance().harvesters = getHarvesters(SonarCore.logger, asmDataTable);
		PlantingHandler.instance().fertilisers = getFertilisers(SonarCore.logger, asmDataTable);
	}

	public static List<ITileEnergyHandler> getTileEnergyHandlers(Logger logger, @Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(logger, asmDataTable, TileEnergyHandler.class, ITileEnergyHandler.class, true, true);
	}

	public static List<ISonarFluidHandler> getFluidHandlers(Logger logger, @Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(logger, asmDataTable, FluidHandler.class, ISonarFluidHandler.class, true, true);
	}

	public static List<IItemEnergyHandler> getItemEnergyHandlers(Logger logger, @Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(logger, asmDataTable, ItemEnergyHandler.class, IItemEnergyHandler.class, true, true);
	}

	public static List<ISonarPlanter> getPlanters(Logger logger, @Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(logger, asmDataTable, ASMPlanter.class, ISonarPlanter.class, true, true);
	}

	public static List<ISonarHarvester> getHarvesters(Logger logger, @Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(logger, asmDataTable, ASMHarvester.class, ISonarHarvester.class, true, true);
	}

	public static List<ISonarFertiliser> getFertilisers(Logger logger, @Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(logger, asmDataTable, ASMFertiliser.class, ISonarFertiliser.class, true, true);
	}


	public static void log(Logger logger, ASMLog log, Class type, ASMData asm, String modid) {
		switch (log) {
		case ERROR:
                logger.error(type.getSimpleName() + " couldn't be loaded as an error occurred - please report to mod author: " + asm.getClassName());
			break;
		case LOADED:
               logger.info(type.getSimpleName() + " loaded successfully: " + asm.getClassName());
			break;
		case MODID:
                logger.info("Couldn't load " + type.getSimpleName() + " " +asm.getClassName() + " for modid " + modid);
			break;
		default:
			break;
		}
	}

	public static <T> List<T> getInstances(Logger logger, @Nonnull ASMDataTable asmDataTable, Class annotation, Class<T> instanceClass, boolean checkModid, boolean sortPriority) {
        List<T> instances = new ArrayList<>();

		String annotationClassName = annotation.getCanonicalName();
		Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
		ArrayList<ASMData> data = new ArrayList<>(asmDatas);

        if (sortPriority)
            data.sort(SORT_PRIORITY);
		for (ASMDataTable.ASMData asmData : data) {
			String modid = checkModid ? (String) asmData.getAnnotationInfo().get("modid") : "";
			if (!checkModid || Loader.isModLoaded(modid) || Loader.isModLoaded(modid.toLowerCase())) {
				try {
					Class<?> asmClass = Class.forName(asmData.getClassName());
					Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
					T instance = asmInstanceClass.newInstance();
					instances.add(instance);
					log(logger, ASMLog.LOADED, instanceClass, asmData, modid);
				} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
					log(logger, ASMLog.ERROR, instanceClass, asmData, modid);
				}
			} else {
				log(logger, ASMLog.MODID, instanceClass, asmData, modid);
			}
		}

		return instances;
	}

    public static <T> void injectInstances(Logger logger, ASMDataTable data, Class annotation, Class<T> instanceClass, Function<ASMData, T> create) {
        for (ASMDataTable.ASMData entry : data.getAll(annotation.getName())) {
            final String targetClass = entry.getClassName();
            final String targetName = entry.getObjectName();
            T i = create.apply(entry);
            if (i == null) {
                return;
            }
            try {
                Field field = Class.forName(targetClass).getDeclaredField(targetName);
                if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
                	logger.warn("Unable to inject instance %s at %s.%s (Non-Static)", i.toString(), targetClass, targetName);
                }
                EnumHelper.setFailsafeFieldValue(field, null, i);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("Unable to inject instance %s at %s.%s", i.toString(), targetClass, targetName);
            }
        }
    }

	public static <T> List<Pair<ASMDataTable.ASMData, Class<? extends T>>> getClasses(Logger logger, @Nonnull ASMDataTable asmDataTable, Class annotation, Class<T> instanceClass, boolean checkModid) {
		String annotationClassName = annotation.getCanonicalName();
		Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
		List<Pair<ASMDataTable.ASMData, Class<? extends T>>> classes = new ArrayList<>();
		for (ASMDataTable.ASMData asmData : asmDatas) {
			String modid = checkModid ? (String) asmData.getAnnotationInfo().get("modid") : "";
			if (!checkModid || Loader.isModLoaded(modid) || Loader.isModLoaded(modid.toLowerCase())) {
				try {
					Class<?> asmClass = Class.forName(asmData.getClassName());
					Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
					classes.add(new Pair(asmData, asmInstanceClass));
					log(logger, ASMLog.LOADED, instanceClass, asmData, modid);
				} catch (ClassNotFoundException e) {
					log(logger, ASMLog.ERROR, instanceClass, asmData, modid);
				}
			} else {
				log(logger, ASMLog.MODID, instanceClass, asmData, modid);
			}
		}
		return classes;
	}
}
