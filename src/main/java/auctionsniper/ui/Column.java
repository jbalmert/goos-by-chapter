package auctionsniper.ui;

import auctionsniper.SniperSnapshot;

/**
 * Added Chapter 15:
 * Code from GOOS, pg 156 , 170
 * - Added Column.name field for populating the JTable column headers labels.
 */
public enum Column {
    ITEM_IDENTIFIER("Item") {
        @Override
        public Object valueIn(SniperSnapshot snapshot) {
            return snapshot.itemId;
        }
    },
    LAST_PRICE("Last Price") {
        @Override
        public Object valueIn(SniperSnapshot snapshot) {
            return snapshot.lastPrice;
        }
    },
    LAST_BID("Last Bid") {
        @Override
        public Object valueIn(SniperSnapshot snapshot) {
            return snapshot.lastBid;
        }
    },
    SNIPER_STATUS("State") {
        @Override
        public Object valueIn(SniperSnapshot snapshot) {
            return SnipersTableModel.textFor(snapshot.state);
        }
    };
    public final String name;

    public static Column at(int offset) {
        return values()[offset];
    }

    private Column(String name) {
        this.name = name;
    }

    abstract Object valueIn(SniperSnapshot snapshot);
}
