package memory;

/**
 *@author Eric Le Fort
 *@version 0.0.1
 */
public class Location{
	private String postalCode;
	private double mappedVal;
	private int country, province, region, county;
	
	/**
	 * Initializes a new <code>Location</code> with the given parameters.
	 * 
	 * @param mappedVal - The mapped value for this location.
	 * @param postalCode - This location's postal code.
	 * @param country - This location's country.
	 * @param province - This location's province.
	 * @param region - This location's region.
	 * @param county - This location's county.
	 */
	public Location(int mappedVal, String postalCode, int country, int province, int region, int county){
		this.mappedVal = mappedVal;
		this.postalCode = postalCode;
		this.country = country;
		this.province = province;
		this.region = region;
		this.county = county;
	}//Constructor
	
	/**
	 * Initializes a new <code>Location</code> with the given parameters.
	 * 
	 * @param mappedVal - The mapped value for this location.
	 * @param country - This location's country.
	 * @param region - This location's region.
	 */
	public Location(int mappedVal, int country, int region){
		this.mappedVal = mappedVal;
		this.country = country;
		this.region = region;
		
		postalCode = "";
		province = -1;
		county = -1;
	}//Constructor
	
	public void setMappedVal(double mappedVal){ this.mappedVal = mappedVal; }//setMappedVal()
	
	public double getMappedVal(){ return mappedVal; }//getMappedVal()
	public String getPostalCode(){ return postalCode; }//getPostalCode()
	public int getCountry(){ return country; }//getCountry()
	public int getProvince(){ return province; }//getProvince()
	public int getRegion(){ return region; }//getRegion()
	public int getCounty(){ return county; }//getCounty()

}//Location