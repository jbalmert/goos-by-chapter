package auctionsniper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Added Chapter 18:
 * Code from GOOS, pg 209
 * - This bundles up the item id and stop price for the item together.
 *
 */
public class Item {
    public final String identifier;
    public final int stopPrice;

    public Item(String identifier, int stopPrice) {
        this.identifier = identifier;
        this.stopPrice = stopPrice;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object that) {
         return EqualsBuilder.reflectionEquals(this, that);
    }

    public boolean allowsBid(int bid) {
        return bid <= stopPrice;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


}
