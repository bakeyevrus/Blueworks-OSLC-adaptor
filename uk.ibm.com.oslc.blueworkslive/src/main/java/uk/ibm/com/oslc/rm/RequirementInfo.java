package uk.ibm.com.oslc.rm;

import java.util.Date;
import java.util.List;

public class RequirementInfo {

	private String id;
	private String name;
	private String type;
	private String documentation;
	private Date lastModified;
	private String subType;
	private String parent;
	private List<RequirementInfo> children;
	private String lastModifiedBy;
	
	public RequirementInfo(String id, String name, String type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	public RequirementInfo(String id, String name, String type,
			String documentation, Date lastModified, String subType,
			String parent, List<RequirementInfo> children) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.documentation = documentation;
		this.lastModified = lastModified;
		this.subType = subType;
		this.parent = parent;
		this.children = children;
	}

	public RequirementInfo(String id, String name, String type,
			String documentation, Date lastModified, String lastModifiedBy, String subType,
			String parent, List<RequirementInfo> children) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.documentation = documentation;
		this.lastModified = lastModified;
		this.lastModifiedBy = lastModifiedBy;
		this.subType = subType;
		this.parent = parent;
		this.children = children;
	}
	
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	public String getDocumentation() {
		return documentation;
	}
	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public List<RequirementInfo> getChildren()
	{
		return children;
	}
	
	public void addChild(RequirementInfo newChild)
	{
		children.add(newChild);
	}

	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	
	
}
