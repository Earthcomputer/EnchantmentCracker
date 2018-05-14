package enchcracker;

import java.util.ArrayList;

public class EnchData{
  private int shelves = -1;
  private int s1 = -1;
  private int s2 = -1;
  private int s3 = -1;

  public EnchData(int shelves,int s1,int s2,int s3){
    this.shelves=shelves;
    this.s1=s1;
    this.s2=s2;
    this.s3=s3;
  }

  @Override
  public String toString(){
    return String.format("%2d   bookshelves:     %2d     %2d     %2d%n",this.shelves,this.s1,this.s2,this.s3);
  }
}
