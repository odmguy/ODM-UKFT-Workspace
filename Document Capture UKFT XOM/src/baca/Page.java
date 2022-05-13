package baca;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Page {

	public Block[] BlockList;
	public Table[] TableList;
	public int TotalLetters;
	public int TotalGoodLetters;
	public Barcode[] BarcodeList;
	public PageInfo PageInfo;
	public KVP[] KVPTable;
	public TableLineItems[] TableLineItems;
	public Header[] HeaderList;

	// --- Constructor
	public Page() {
	}
	
	// -------- Utility Methods ---------
	
	private boolean DEBUG = false; 

	// -- @JsonIgnore is needed to stop this becoming a required field on the JSON input parameter object
	// -- and it will also be generated in the default payload in the REST tester in the RES console and cause an error!!!
	// -- Prefixing a method with 'get' will trigger auto verbalisation as 'the page number of ...' - JavaBeans Style
	@JsonIgnore
	public int getPageNumber() {   // -- Return the page number of the doc starting from 1 instead of 0
		// -- Auto     {page number} of {this}
		return PageInfo.PageNo + 1;
	}
	
	@JsonIgnore
	public int getBlockIndex(Block block) {   // -- Business method so returns in range 1..n
		// -- MANUAL      the index of {0} on {this}
		for (int bloop = 0; bloop < BlockList.length; bloop++) {
			if (block == BlockList[bloop])
				return bloop+1; // -- Business numbering
		}
		return 0;
	}
	
	
	private String pageTextCache = null; // -- Initialised the first time getCompleteText is called as it may be called often
	@JsonIgnore
	public String getCompleteText() {   // -- Get's all the text in blocks, Tables, Headers etc.
		// -- TODO: Include all KVPs and TableLineItems ???
		// -- Auto Verbalises as       the complete text of {this}
		if(pageTextCache != null) {
			return pageTextCache; // -- Already initialised so return it.
		}
		else {  // -- First access time so build string from all words 
			pageTextCache = "";
			for (Header h: HeaderList ) {
				pageTextCache = pageTextCache + h.HeaderText + " ";
			}
			for (Block b: BlockList ) {
				pageTextCache = pageTextCache + b.getCompleteText() + " ";
			}
			for (Table t: TableList ) {
				for (TableRow tr: t.RowList) {
					pageTextCache = pageTextCache + tr.getCompleteText()+ " ";
				}
			}
			return pageTextCache;
		}
	}

	// *******************************************************
	//    Multi-Column Detection Stuff
	// *******************************************************
	/*********
	public boolean columnsDetected = false;
	public int leftColumnXStart;
	public int leftColumnXEnd;
	public int rightColumnXStart;
	public int rightColumnXEnd;
	public String leftColumnText = "";
	public String rightColumnText = "";
	public String crossColumnText = "";
********/
	
	public Block nextBlock(Block currentBlock) {
		// --  the following block to {0} on {this}
		// -- TODO - Implement header/footer skipping and first block from next page getting
		if (currentBlock.BlockID.equals(BlockList[BlockList.length - 1].BlockID))
			return null; // -- We are on the last block of the page

		for (int bindex = 0; bindex < BlockList.length; bindex++) {
			if (BlockList[bindex].BlockID.equals(currentBlock.BlockID)) {
				return BlockList[bindex + 1]; // -- Return the immediate next block (which may be a footer)
			}
		}
		return null; // -- Should never hit this as we should always find the current block on this page.
	}
	
	
	// -- we have to pass all these args to get access to the next page (TODO would be nicer to store a reference to it in this class) 
	public Block nextContinuingParagraph(Block currentBlock, Document document) {
		// -- Verbalise as          the next continuing paragraph to {0,<block>} on {this} in {1,<doc>}
		if (DEBUG) System.out.println("~~~ getNextContinuationBlock : checking for continuation of " + currentBlock.BlockID + " on real page num "+ getPageNumber());
		Block next = null;
		boolean lastBlock = false;
		boolean penultimateBlock = false;
		if (currentBlock.BlockID.equals(BlockList[BlockList.length - 1].BlockID))
			lastBlock = true;
		else if (BlockList.length > 1 && currentBlock.BlockID.equals(this.BlockList[BlockList.length - 2].BlockID))
			penultimateBlock = true;

		if (lastBlock) {
			// -- We're on the last block of the page so check if first "real" block on next page matches this
			// -- Ignore any short header block at top of next page
			// -- TODO - TEST this
			Block nextPageBlock = nextPageBlock(document);
			if (nextPageBlock != null && blockSizesMatch(currentBlock, nextPageBlock) ) {
				if (DEBUG) System.out.println("~~~      getNextContinuationBlock : FOUND continuing block on NEXT page - "+nextPageBlock.BlockID);
				return nextPageBlock;
			}
			else {
				return null;
			}
			
		} else if (penultimateBlock) {
			// -- We're on the penultimate block of the page so check if the final block
			// matches
			next = BlockList[BlockList.length - 1]; // last block on this page
			if (blockSizesMatch(currentBlock, next)) {
				// -- last block matches so it looks real so return it
				if (DEBUG) System.out.println("~~~      getNextContinuationBlock : Penultimate block on page - and last CONTINUES");
				return next;
				
			} else if (blockIsFooter(next)) {
				// -- Following block is a footer, ignore it and get first real block from next page if any
				if (DEBUG) System.out.println("~~~      getNextContinuationBlock : Penultimate block on page - and last is ignored Footer - Checking Next page");
				Block nextPageBlock = nextPageBlock(document);
				if (nextPageBlock != null && blockSizesMatch(currentBlock, nextPageBlock) ) {
					if (DEBUG) System.out.println("~~~      getNextContinuationBlock : FOUND continuing block on NEXT page - "+nextPageBlock.BlockID);
					return nextPageBlock;
				}
				else {
					if (DEBUG) System.out.println("~~~      getNextContinuationBlock : NEXT page is not a continuation");
					return null;
				}
				
			} else { // -- Final block is not a match so return nothing
				if (DEBUG) System.out.println("~~~      getNextContinuationBlock : Penultimate block on page - and last is not related");
				return null;
			}
		} else { // -- Find the next block and check if it is at the same indentation
			for (int bindex = 0; bindex < BlockList.length; bindex++) {
				if (BlockList[bindex].BlockID.equals(currentBlock.BlockID)) {
					next = BlockList[bindex + 1];
					break;
				}
			}
			
			if (blockSizesMatch(currentBlock, next)) {
				if (DEBUG) System.out.println("~~~      getNextContinuationBlock : " + next.BlockID+ " CONTINUES from previous mid-page block");
				return next;
			} else {
				if (DEBUG) System.out.println("~~~      getNextContinuationBlock : next block " + next.BlockID+ " is NOT a continuation");
				return null;
			}

		}
	}

	
	private boolean blockSizesMatch(Block blockA, Block blockB) {
		if (DEBUG && (blockA == null || blockB == null) ) {
			System.out.println("~~~    blockSizesMatch : One of the blocks is NULL which should not happen");
			return false;
		}
		double FUDGEFACTOR = 125; // -- roughly 5% of regular page width (2480) // -- Does this vary between docs???
		double xA = blockA.BlockStartX;
		double xB = blockB.BlockStartX;
		double wA = blockA.BlockWidth;
		double wB = blockB.BlockWidth;
/****
		double xA = Double.parseDouble(blockA.BlockStartX);
		double xB = Double.parseDouble(blockB.BlockStartX);
		double wA = Double.parseDouble(blockA.BlockWidth);
		double wB = Double.parseDouble(blockB.BlockWidth);
****/		
		// -- Start X coordinates of paragraphs differ OR width of 2nd paragraph more than 50% wider  
 		if ( Math.abs( xA - xB) > FUDGEFACTOR 
 				|| wB > wA * 1.5) 
			return false;
		else
			return true;
		// -- TODO - Tune this algorithm
	}
	
	private boolean blockIsHeader(Block block) {
		// -- One line of text in the top 8% section of the page
		if (block.LineList.length == 1 &&
				(block.BlockStartY / PageInfo.PageHeight) < 0.08) {
//			(Double.parseDouble(block.BlockStartY) / PageInfo.PageHeight) < 0.08) {
			if (DEBUG) System.out.println("~~~   "+block.BlockID+" is a header.");
			return true;
		}
		else
			return false;
	}
	
	private boolean blockIsFooter(Block block) {
		// -- One line of text in the bottom 92% section of the page
		if (block.LineList.length == 1 &&
				(block.BlockStartY / PageInfo.PageHeight) > 0.92) {
//			(Double.parseDouble(block.BlockStartY) / PageInfo.PageHeight) > 0.92) {
			if (DEBUG) System.out.println("~~~   "+block.BlockID+" is a footer.");
			return true;
		}
		else
			return false;
	}

	// -- Get the first real paragraph on the following page to this paragraph
	private Block nextPageBlock(Document document) {
		// --     
		int nextPageindex = PageInfo.PageNo + 1;
		if (nextPageindex >  document.pageList.length - 1) {
			return null; // -- We're on the last page so there is no Next Paragraph
		}
		
		Page nextPage = document.pageList[nextPageindex];
		if (nextPage.BlockList.length > 1 && blockIsHeader(nextPage.BlockList[0]) ) 
			return nextPage.BlockList[1]; // -- First is Header so return 2nd block
		else if (nextPage.BlockList.length >= 1)
			return nextPage.BlockList[0]; // -- No header so return 1st block
		else	
			return null;	// -- No blocks on page
	}
	
	
	
	// --------------------------------------
	public boolean blocksHaveSameIndentation(Block blockA, Block blockB) {
		// -- @BusinessVerbalisation("blocks {0} and {1} have same indentation on {this}")
		// -- True if the Start X coordinates of the two blocks are the same or very close
		double FUDGEFACTOR = 10; // -- Page width is in the region of 2500 units
		double xA = blockA.BlockStartX;
		double xB = blockB.BlockStartX;
//		double xA = Double.parseDouble(blockA.BlockStartX);
//		double xB = Double.parseDouble(blockB.BlockStartX);
		// -- Start X coordinates of paragraphs differ OR width of 2nd paragraph more than 50% wider  
 		if ( Math.abs( xA - xB) <= FUDGEFACTOR )
			return true;
		else
			return false;
	}

	public boolean blocksHaveSameWidth(Block blockA, Block blockB) {
		// -- @BusinessVerbalisation("blocks {0} and {1} have same width on {this}")
		// -- True if the width two blocks are the same or very close
		double FUDGEFACTOR = 20; // -- Page width is in the region of 2500 units
		double wA = blockA.BlockWidth;
		double wB = blockB.BlockWidth;
//		double wA = Double.parseDouble(blockA.BlockWidth);
//		double wB = Double.parseDouble(blockB.BlockWidth);
		// -- Start X coordinates of paragraphs differ OR width of 2nd paragraph more than 50% wider  
 		if ( Math.abs( wA - wB) <= FUDGEFACTOR )
			return true;
		else
			return false;
	}
	
}
