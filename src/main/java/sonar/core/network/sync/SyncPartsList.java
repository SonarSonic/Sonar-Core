package sonar.core.network.sync;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.world.IWorldNameable;
import sonar.core.SonarCore;

public class SyncPartsList extends ArrayList<ISyncPart> {

	public final String build;
	
	public SyncPartsList(String builder){
		this.build = builder;
	}
	
	public ArrayList<String> tags = new ArrayList();

	public boolean add(ISyncPart part) {
		if (tags.contains(part.getTagName())) {
			SonarCore.logger.error(build + ": Two sync parts have matching tag names!" + part.getTagName());
		}
		return super.add(part);
	}

	public boolean addAll(Collection<? extends ISyncPart> parts) {
		for(ISyncPart part : parts){
			if(part!=null && tags.contains(part.getTagName())){
				SonarCore.logger.error(build + ": Two sync parts have matching tag names!" + part.getTagName());
			}
		}
		return super.addAll(parts);
	}
	
	public ISyncPart getPartByID(int id){
		return getPartByTagName(String.valueOf(id));
	}
	
	public ISyncPart getPartByTagName(String tag){
		for(ISyncPart part :this){
			if(part.getTagName().equals(tag)){
				return part;
			}
		}
		return null;
	}
}
