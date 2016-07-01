package datasets;

/**
 *
 * @author ron
 */
public class Invoice
{
    private int id;
    private long timestamp;
    private int orderId;
    private int quantityItem;
    private String itemDesc;
    private float itemUnitPrice;
    private float itemVATPercentage;
    private float itemQuantityPrice;
    private float subTotalB4Discount;
    private int customerDiscount;
    private float subTotal;
    private float vat;
    private float total;
    private float paid;

    /**
     *
     */
    public Invoice()
    {
	id = 0;
	timestamp = 0;
	orderId = 0;
	quantityItem = 0;
	itemDesc = "";
	itemUnitPrice = 0;
	itemVATPercentage = 0;
	itemQuantityPrice = 0;
	subTotalB4Discount = 0;
	customerDiscount = 0;
	subTotal = 0;
	vat = 0;
	total = 0;
	paid = 0;
    }

    /**
     *
     * @param idParam
     * @param timestampParam
     * @param orderIdParam
     * @param quantityItemParam
     * @param itemDescParam
     * @param itemUnitPriceParam
     * @param itemVATPercentageParam
     * @param itemQuantityPriceParam
     * @param subTotalB4DiscountParam
     * @param customerDiscountParam
     * @param subTotalParam
     * @param vatParam
     * @param totalParam
     * @param paidParam
     */
    public Invoice(
			int idParam,
			long timestampParam,
			int orderIdParam,
			int quantityItemParam,
			String itemDescParam,
			float itemUnitPriceParam,
			float itemVATPercentageParam,
			float itemQuantityPriceParam,
			float subTotalB4DiscountParam,
			int customerDiscountParam,
			float subTotalParam,
			float vatParam,
			float totalParam,
			float paidParam
		    )
		    {
			id = idParam;
			timestamp = timestampParam;
			orderId = orderIdParam;
			quantityItem = quantityItemParam;
			itemDesc = itemDescParam;
			itemUnitPrice = itemUnitPriceParam;
			itemVATPercentage = itemVATPercentageParam;
			itemQuantityPrice = itemQuantityPriceParam;
			subTotalB4Discount = subTotalB4DiscountParam;
			customerDiscount = customerDiscountParam;
			subTotal = subTotalParam;
			vat = vatParam;
			total = totalParam;
			paid = paidParam;
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
    public long	    getTimestamp()				    { return timestamp; }

    /**
     *
     * @return
     */
    public int	    getOrderId()				    { return orderId; }

    /**
     *
     * @return
     */
    public int	    getQuantityItem()				    { return quantityItem; }

    /**
     *
     * @return
     */
    public String   getItemDesc()				    { return itemDesc; }

    /**
     *
     * @return
     */
    public float    getItemUnitPrice()				    { return itemUnitPrice; }

    /**
     *
     * @return
     */
    public float    getItemVATPercentage()			    { return itemVATPercentage; }

    /**
     *
     * @return
     */
    public float    getItemQuantityPrice()			    { return itemQuantityPrice; }

    /**
     *
     * @return
     */
    public float    getSubTotalB4Discount()			    { return subTotalB4Discount; }

    /**
     *
     * @return
     */
    public int	    getCustomerDiscount()			    { return customerDiscount; }

    /**
     *
     * @return
     */
    public float    getSubTotal()				    { return subTotal; }

    /**
     *
     * @return
     */
    public float    getVAT()					    { return vat; }

    /**
     *
     * @return
     */
    public float    getTotal()					    { return total; }

    /**
     *
     * @return
     */
    public float    getPaid()					    { return paid; }

    /**
     *
     * @param idParam
     */
    public void	    setId(int idParam)					    { id = idParam; }

    /**
     *
     * @param timestampParam
     */
    public void	    setTimestamp(long timestampParam)			    { timestamp = timestampParam; }

    /**
     *
     * @param orderIdParam
     */
    public void	    setOrderId(int orderIdParam)			    { orderId = orderIdParam; }

    /**
     *
     * @param quantityItemParam
     */
    public void	    setQuantityItem(int quantityItemParam)		    { quantityItem = quantityItemParam; }

    /**
     *
     * @param itemDescParam
     */
    public void     setItemDesc(String itemDescParam)			    { itemDesc = itemDescParam; }

    /**
     *
     * @param itemUnitPriceParam
     */
    public void     setItemUnitPrice(float itemUnitPriceParam)		    { itemUnitPrice = itemUnitPriceParam; }

    /**
     *
     * @param itemVATPercentageParam
     */
    public void	    setItemVATPercentage(float itemVATPercentageParam)	    { itemVATPercentage = itemVATPercentageParam; }

    /**
     *
     * @param itemQuantityPriceParam
     */
    public void     setItemQuantityPrice(float itemQuantityPriceParam)	    { itemQuantityPrice = itemQuantityPriceParam; }

    /**
     *
     * @param subTotalB4DiscountParam
     */
    public void     setSubTotalB4Discount(float subTotalB4DiscountParam)    { subTotalB4Discount = subTotalB4DiscountParam; }

    /**
     *
     * @param customerDiscountParam
     */
    public void	    setCustomerDiscount(int customerDiscountParam)	    { customerDiscount = customerDiscountParam; }

    /**
     *
     * @param subTotalParam
     */
    public void     setSubTotal(float subTotalParam)			    { subTotal = subTotalParam; }

    /**
     *
     * @param vatParam
     */
    public void     setVAT(float vatParam)				    { vat = vatParam; }

    /**
     *
     * @param totalParam
     */
    public void     setTotal(float totalParam)				    { total = totalParam; }

    /**
     *
     * @param paidParam
     */
    public void     setPaid(float paidParam)				    { paid = paidParam; }
}
