package items;

public final class ItemStack {
    public String itemId;
    public int count;

    public ItemStack() { this(null,0); }
    public ItemStack(String id, int c) { this.itemId=id; this.count=c; }

    public boolean isEmpty() { return itemId==null || count<=0; }
    public void clear() { itemId=null; count=0; }
    public ItemStack copy() { return new ItemStack(itemId, count); }
}
