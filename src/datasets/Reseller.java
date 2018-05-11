/*
 * Copyright © 2008 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/ ; either
 * version 4.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International Public License for more details.
 *
 * You should have received a copy of the Creative Commons 
 * Public License License along with this software;
 */

package datasets;

/**
 *
 * @author ron
 */
public class Reseller
{
    private int id;
    private long timestamp;
    private String companyName;
    private String address;
    private String addressNr;
    private String postcode;
    private String city;
    private String country;
    private String contactName;
    private String phoneNr;
    private String mobileNr;
    private String email;
    private String password;
    private int resellerDiscount;
    private String comment;

    /**
     *
     */
    public Reseller()
    {
	id		    = 0;
	timestamp	    = 0;
	companyName	    = "";
        address             = "";
        addressNr           = "";
        postcode	    = "";
        city		    = "";
        country		    = "";
        contactName	    = "";
        phoneNr		    = "";
        mobileNr	    = "";
	email		    = "";
	password	    = "";
	resellerDiscount    = 0;
	comment		    = "";
    }

    /**
     *
     * @param idParam
     * @param timestampParam
     * @param companyNameParam
     * @param addressParam
     * @param addressNrParam
     * @param postcodeParam
     * @param cityParam
     * @param countryParam
     * @param contactNameParam
     * @param phoneNrParam
     * @param mobileNrParam
     * @param emailParam
     * @param passwordParam
     * @param resellerDiscountParam
     * @param commentParam
     */
    public Reseller(
			int idParam,
			long timestampParam,
			String companyNameParam,
			String addressParam,
			String addressNrParam,
			String postcodeParam,
			String cityParam,
			String countryParam,
			String contactNameParam,
			String phoneNrParam,
			String mobileNrParam,
			String emailParam,
			String passwordParam,
			int resellerDiscountParam,
			String commentParam
		    )
		    {
			id		    = idParam;
			timestamp	    = timestampParam;
			companyName	    = companyNameParam;
			address		    = addressParam;
			addressNr	    = addressNrParam;
			postcode	    = postcodeParam;
			city		    = cityParam;
			country		    = countryParam;
			contactName	    = contactNameParam;
			phoneNr		    = phoneNrParam;
			mobileNr	    = mobileNrParam;
			email		    = emailParam;
			password	    = passwordParam;
			resellerDiscount    = resellerDiscountParam;
			comment		    = commentParam;
		    }

    /**
     *
     * @return
     */
    public int	    getId()					    { return id; }

    /**
     *
     * @return
     */
    public long     getTimestamp()				    { return timestamp; }

    /**
     *
     * @return
     */
    public String   getCompanyName()				    { return companyName; }

    /**
     *
     * @return
     */
    public String   getAddress()				    { return address; }

    /**
     *
     * @return
     */
    public String   getAddressNr()				    { return addressNr; }

    /**
     *
     * @return
     */
    public String   getpostcode()				    { return postcode; }

    /**
     *
     * @return
     */
    public String   getCity()					    { return city; }

    /**
     *
     * @return
     */
    public String   getCountry()				    { return country; }

    /**
     *
     * @return
     */
    public String   getContactName()				    { return contactName; }

    /**
     *
     * @return
     */
    public String   getPhoneNr()				    { return phoneNr; }

    /**
     *
     * @return
     */
    public String   getMobileNr()				    { return mobileNr; }

    /**
     *
     * @return
     */
    public String   getEmail()					    { return email; }

    /**
     *
     * @return
     */
    public String   getPassword()				    { return password; }

    /**
     *
     * @return
     */
    public int	    getResellerDiscount()			    { return resellerDiscount; }

    /**
     *
     * @return
     */
    public String   getComment()				    { return comment; }

    /**
     *
     * @param idParam
     */
    public void	    setId(int idParam)				    { id = idParam; }

    /**
     *
     * @param timestampParam
     */
    public void     setTimestamp(long timestampParam)		    { timestamp = timestampParam; }

    /**
     *
     * @param companyNameParam
     */
    public void	    setCompanyName(String companyNameParam)	    { companyName = companyNameParam; }

    /**
     *
     * @param addressParam
     */
    public void     setAddress(String addressParam)		    { address = addressParam; }

    /**
     *
     * @param addressNrParam
     */
    public void     setAddressNr(String addressNrParam)		    { addressNr = addressNrParam; }

    /**
     *
     * @param postcodeParam
     */
    public void     setpostcode(String postcodeParam)		    { postcode = postcodeParam; }

    /**
     *
     * @param cityParam
     */
    public void     setCity(String cityParam)			    { city = cityParam; }

    /**
     *
     * @param countryParam
     */
    public void     setCountry(String countryParam)		    { country = countryParam; }

    /**
     *
     * @param contactNameParam
     */
    public void     setContactName(String contactNameParam)	    { contactName = contactNameParam; }

    /**
     *
     * @param phoneNrParam
     */
    public void     setPhoneNr(String phoneNrParam)		    { phoneNr = phoneNrParam; }

    /**
     *
     * @param mobileNrParam
     */
    public void     setMobileNr(String mobileNrParam)		    { mobileNr = mobileNrParam; }

    /**
     *
     * @param emailParam
     */
    public void     setEmail(String emailParam)			    { email = emailParam; }

    /**
     *
     * @param passwordParam
     */
    public void     setPassword(String passwordParam)		    { password = passwordParam; }

    /**
     *
     * @param resellerDiscountParam
     */
    public void     setResellerDiscount(int resellerDiscountParam)  { resellerDiscount = resellerDiscountParam; }

    /**
     *
     * @param commentParam
     */
    public void     setComment(String commentParam)		    { comment = commentParam; }

    @Override
    public String   toString()
    {
        String info = new String("");
	info += "id: " + id + "<br />\n";
	info += "timestamp: " + timestamp + "<br />\n";
	info += "companyName: " + companyName + "<br />\n";
	info += "address: " + address + "<br />\n";
	info += "addressNr: " + addressNr + "<br />\n";
	info += "postcode: " + postcode + "<br />\n";
	info += "city: " + city + "<br />\n";
	info += "country: " + country + "<br />\n";
	info += "contactName: " + contactName + "<br />\n";
	info += "phoneNr: " + phoneNr + "<br />\n";
	info += "mobileNr: " + mobileNr + "<br />\n";
	info += "email: " + email + "<br />\n";
	info += "password: " + password + "<br />\n";
	info += "resellerDiscount: " + resellerDiscount + "<br />\n";
	info += "comment: " + comment + "<br />\n";
        return info;
    }
}
