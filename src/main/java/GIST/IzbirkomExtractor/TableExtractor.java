/**
 * Copyright © 2013 , UT-Battelle, LLC
 * All rights reserved
 *                                        
 * JavaParserOfAddresses, Version 1.0
 * http://github.com/sorokine/JavaParserOfAddresses
 * 
 * This program is freely distributed under UT-Batelle, LLC
 * open source license.  Read the file LICENSE.txt for details.
 */

package GIST.IzbirkomExtractor;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Alex Sorokine <sorokine@gmail.com>
 *
 */
public class TableExtractor {
	
	/**
	 * Main logger
	 */
	private final static Logger logger = Logger.getLogger("ALL");
	
	private AddressParserManager apm;
	private AddressMatcher addressMatcher;
	private ResultSink resultSink;

	public TableExtractor(AddressMatcher addressMatcher,
			ResultSink resultSink) throws InstantiationException, IllegalAccessException {
		this.addressMatcher = addressMatcher;
		this.resultSink = resultSink;
		this.apm = new AddressParserManager();
		apm.loadAllParsers();
	}

	public void processHTMLfile(File input_html) throws IOException, TableExtractorException, CloneNotSupportedException, SQLException, ResultSinkException {
		
    	logger.info("Start processing " + input_html);
    	
		Document doc = Jsoup.parse(input_html, "UTF-8");
		Elements tables = doc.getElementsByTag("table");
		
		/* count of parseable tables found */
		int tables_found = 0;
		
		/* determine raion name */
		String raion_name = extractRaionFromFileName(input_html.getName());
		//System.err.println(raion_name);
		
		// TODO: inflect raion name in именительный case
		
		/* searches for a table that has "№№ избир. уч-ка" in its very 1st cell */
		for (Element table : tables) {
			Elements rows = table.getElementsByTag("tr");
			boolean firstRow = true;
			
			row_loop:
			for (Element row : rows) {
				Elements cells = row.getElementsByTag("td");
				
				if (firstRow) {
					//System.err.println(row.text());
					if (isParsableTable(row)) {
						firstRow = false;
						logger.info("Processing table #" 
								+ ++tables_found + " in " + input_html);
					} else
						break row_loop;
				}
						
				if (StringUtils.getLevenshteinDistance(cleanupUNICODE(cells.first().text()), "№№ избир. уч-ка") < 3) 
					continue row_loop; /* skip the row if it looks like a table header */

				/* skip rows with all cells empty */
				boolean emptyRow = true;
				for (Element cell : cells) 
					emptyRow = emptyRow && cleanupUNICODE(cell.text()).isEmpty();
				if (emptyRow) continue;
				
				int i_cell = 0;
				Element station_id = null;
				Element address_field = null;
				Element org_address = null; /* address of the комиссия */
				Element station_address = null;
				
				for (Element cell : cells) {
					switch (i_cell) {
					case 0:
						station_id = cell;
						break;
					case 1:
						address_field = cell;
						break;
					case 2:
						org_address = cell;
						break;
					case 3:
						station_address = cell;
					default:
						break;
					}
					i_cell++;
				}
				
				if (station_id == null)
					throw new TableExtractorException("Polling station ID not found", row, input_html);
				if (address_field == null)
					throw new TableExtractorException("Address list not found", row, input_html);
				
				/* extract int from poll station id */
				int psid;
				try {
					psid = Integer.valueOf(cleanupUNICODE(station_id.text()).trim().replaceAll("[^\\d]", ""));
				} catch (NumberFormatException e) {
					Exception te = new TableExtractorException("Failed to parse polling station ID >"+cleanupUNICODE(station_id.text()).trim()+"<: ", station_id, input_html);
					logger.severe(te.getMessage() + "; rest of " + input_html + " ignored.");
					return;
				}
				
				/* extraction from HTML completely finished, now we work only with the addresses in the text form */
				extractAddressesFromText(
						raion_name.trim(), 
						psid, 
						cleanLeftoverHTML(address_field),
						cleanLeftoverHTML(org_address),
						cleanLeftoverHTML(station_address)
					);
			}
		}
		
		if (tables_found == 0) 
			logger.severe("No parsable tables found in " + input_html);
		resultSink.commit();

    	logger.info("" + tables_found + " table(s) processed in " + input_html);
	}

	/**
	 * Cleaning up leftover of HTML code from the cell content.
	 * 
	 * @param cell_content HTML code contains in the table cell 
	 * @return an array list containing each line of the cell_content withh all HTML markup removed
	 */
	private ArrayList<String> cleanLeftoverHTML(Element cell_content) {
		
		ArrayList<String> streets_and_numbers = new ArrayList<String>();
		
		/* <div>s designate separate lines inside the table cell */
		for (Element addr_line : cell_content.getElementsByTag("div")) {
			
			/* skip empty address lines */
			String addr_line_text = cleanupUNICODE(addr_line.text());
			if (StringUtils.isBlank(addr_line_text)) continue;
			
			/* <strong> is not particularly useful, but can designate placement of simple separators like space */
			Elements streets = addr_line.getElementsByTag("strong");
			if (!streets.isEmpty()) {
				addr_line_text = addr_line_text.replaceFirst(Pattern.quote(streets.text()),	" " + streets.text() + " ");
			}
			
			streets_and_numbers.add(addr_line_text);
		}
		return streets_and_numbers;
	}

	/**
	 * Extracts raion name for 'like' query on name or okato columns in the database.
	 * @param fileName file name in the form "Сведения об избирательных участках Нижегородского района.html"
	 * @return
	 */
	private static String extractRaionFromFileName(String fileName) {
		/**
		 * You probably wondering what NFC is and what the next line is doing.
		 * It is only relevant to OSX file names.  For details see
		 * http://stackoverflow.com/questions/3610013/file-listfiles-mangles-unicode-names-with-jdk-6-unicode-normalization-issues
		 */
		String fileNameNFC = Normalizer.isNormalized(fileName, Normalizer.Form.NFC) ? 
				fileName : Normalizer.normalize(fileName, Normalizer.Form.NFC);
		return 
				fileNameNFC.replaceFirst("\\.html?$", ""). /* remove parts of the file name */
				replaceFirst("^Сведения об избирательн.* участк\\S+\\s+", "").
//				replaceAll("\\s*\\bрайона\\b\\s*", ""). /* THIS IS NFD FROM remove word "района" and spaces*/
				replaceAll("\\s*\\bрайона\\b\\s*", ""). /* THIS IS NFC FORM remove word "района" and spaces*/
				replace('ё', 'е').replace('Ё', 'Е'). /* replace ёЁ */
				replaceAll("(ого|ое|ый|ий|ой)$", ""); /* remove окончания ого, ое, ый, ой */
	}

	/**
	 * For tests, ignore.
	 * @param argv
	 */
	public static void main(String[] argv) {
		System.out.println(extractRaionFromFileName("Сведения об избирательных участках района Беговой.html"));
	}
	
	/**
	 * Address tokenizer 
	 * @param psid polling station ID
	 * @param raion_name  
	 *			
	 * @param streets_and_numbers
	 * @param station_address 
	 * @param org_address 
	 * @throws CloneNotSupportedException
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ResultSinkException 
	 */
	private void extractAddressesFromText(
			String raion_name, 
			int psid, 
			ArrayList<String> streets_and_numbers, 
			ArrayList<String> org_address, 
			ArrayList<String> station_address
		) throws CloneNotSupportedException, IOException, ResultSinkException, SQLException {
		
		/* split the address line on ; in case there is a street name somewhere in the middle; works for most cases */
		ArrayList<String> streets_and_numbers_split = new ArrayList<String>();
		for (String addr_line_text_raw : streets_and_numbers) {
			
			// FIXME: move cleanup to later parsing stages
			String addr_line_text = cleanupUNICODE(addr_line_text_raw).
					replace('.', ' '). // get rid of all periods 
					replaceAll("[:;]\\s*$", ""). // remove spaces and semicolons at the end of the string, colon is a typo fix
					replaceAll("\\s*дд №№?\\s*", " ").
					replaceAll("\\s*дд\\s ", " ").
					replaceAll("\\s*№\\s*", " ").
					replaceAll("\\s*дома? №№?\\s*", " ").
					replace('ё', 'е').replace('Ё', 'Е'). // an ugly way to handle ё
					replaceAll("щоссе", "шоссе").
					replaceAll("\\s*(пос|дер)\\s", "; $1 "). // fixes some missed spaces
					replace("Люксенбург", "Люксембург").
					replace("(общежитие)", "").
					replaceAll("Таллиннская", "Таллинская").
					replaceAll("\\bОренбургска\\b", "Оренбургская").
					replaceAll("Иерсалимская", "Иерусалимская").
					replaceAll("\\s+", " ").trim(); // remove spurious spaces (has to be the last action) 
			
			logger.fine("Levenshtein=" + StringUtils.getLevenshteinDistance(addr_line_text, addr_line_text_raw) + 
					": " + addr_line_text_raw + "' => '" + addr_line_text +	"'");
			
			String addr_parts[] = addr_line_text.split("\\s*;\\s*");
			if (addr_parts.length > 0) {
				for (String s : addr_parts) {
					if (s.isEmpty()) continue;
					streets_and_numbers_split.add(s);
				}
			} else
				streets_and_numbers_split.add(addr_line_text);
		}
		
		/* fixes for incorrectly split lines */
		ArrayList<String> streets_and_numbers_fixed = new ArrayList<String>();
		streets_and_numbers_fixed.add(streets_and_numbers_split.get(0));

		/* these are the characters that can only be found in house number part */
		Pattern house_number_chars_pattern = Pattern.compile("[^0-9(),\\s/;кострапдбА-ЕЩ\\-]"); 			
		
		for (int i = 1; i < streets_and_numbers_split.size(); i++) {
			String cur_line = streets_and_numbers_split.get(i);

			/* last element number of the array of fixed addresses */
			int last_el = streets_and_numbers_fixed.size() - 1;
			String prev_line = streets_and_numbers_fixed.get(last_el);
			
			/* частные домовладения and addresses in Серебряный Бор can be broken across lines */
			if (prev_line.endsWith("частные") && cur_line.startsWith("домовладения") ||
					prev_line.endsWith("Бора") && cur_line.matches("^\\d-я?\\s*линия.+$")) {
				
				streets_and_numbers_fixed.set(last_el, prev_line + " " + cur_line);
				continue;
			} 
			
			/* if a line looks like house numbers only, append it to the previous string */
			Matcher not_only_house_numbers = house_number_chars_pattern.matcher(cur_line);
			if (!not_only_house_numbers.find()) { // double negation  
				
				String infix = prev_line.matches(",\\s*$") || cur_line.matches("^\\s*,") ? "" : ", ";
				streets_and_numbers_fixed.set(last_el, prev_line + infix + cur_line);
				continue;
			} 
			
			streets_and_numbers_fixed.add(cur_line);
		}
		
		for (String street_name_and_numbers : streets_and_numbers_fixed) {
			
			ArrayList<ParsedAddress> pas = apm.parseThroughAllParsers(
					street_name_and_numbers, 
					StringUtils.join(org_address.toArray(), " "),
					StringUtils.join(station_address.toArray(), " "));
			if (pas.size() == 0) {
				logger.severe("Complete parse failure: '" + street_name_and_numbers + 
						"' src='" + StringUtils.join(streets_and_numbers, '|') + 
						"' from " + raion_name);
				continue;
			}
			//System.out.println(pa);
			
			/* matched individual addresses */
			ArrayList<Iterable<IndividualAddress>> matched_ias = new ArrayList<>(pas.size());
			int[] matches = new int[pas.size()]; // count of successful matches per ParsedAddress 
			int matched_parsers = 0; // number of parsers that had successful matches
			
			for (ParsedAddress pa : pas) {
				Iterable<IndividualAddress> ias = pa.enumerateIndividualAddresses();
				matched_ias.add(ias);
				
				if (ias != null)
					for (IndividualAddress individualAddress : ias) {
						if (individualAddress == null) continue;
						
						individualAddress.setRaion(raion_name);
						individualAddress.setPsid(psid);
						individualAddress.setOtherParses(pas);
					
						/* address matcher updates individual address */
						addressMatcher.matchAddress(individualAddress);
						logger.info("" + individualAddress);

						if (individualAddress.getOsmid().size() > 0)
							matches[matched_ias.size()-1]++;
					} 
				else {
					logger.severe("NULL IndividualAddress list in " + pa);
					return;
				}
				
				// increase count of parsers with successful matches
				if (matches[matched_ias.size()-1] > 0)
					matched_parsers++;
			}

			// set secondary match counts
			for (ParsedAddress pa : pas)
				pa.setParserMatchCount(matched_parsers);
			
			/* save result from the highest-priority parser */
			boolean resultPosted = false;
			for (int i = 0; i < matches.length; i++) {
				if (matches[i] > 0) 
					if (resultPosted)
						for (IndividualAddress ia : matched_ias.get(i)) 
							logger.info("Matches in multiple parses in " + ia);
					else {
						for (IndividualAddress ia : matched_ias.get(i)) 
							resultSink.postResult(ia);
						resultPosted = true;
					}
			}
			
			/* if no addresses were matched post results from the highest priority parser */
			if (!resultPosted)
				for (IndividualAddress ia : matched_ias.get(0)) 
					resultSink.postResult(ia);
				
		}
	}

	/**
	 * Tests the row if it looks like the 1st row of a parsable table
	 * @param row
	 * @return
	 */
	private boolean isParsableTable(Element row) {

		Elements cells = row.getElementsByTag("td");
		
		/* number of columns should be 4 */
		if (cells.size() != 4) 
			return false;
		
		/* look for number signs in 1st cell*/
		if (StringUtils.getLevenshteinDistance(cleanupUNICODE(cells.first().text()), "№№ избир. уч-ка") < 3)
			return true;
		
		/* discard the table if any of the cells is empty */
		for (Element cell : cells) {
			if (cleanupUNICODE(cell.text()).isEmpty())
				return false;
		}
		
		/* 1st column should be a number */
		try {
			Integer.parseInt(cleanupUNICODE(cells.first().text()).trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Replaces strange UNICODE symbols with ASCII codes, trims leading and trailing spaces.  The symbols are:
	 *  spaces
	 *  dash
	 * @param text
	 * @return
	 */
	private static String cleanupUNICODE(String text) {
		return 
				text.replaceAll("[\\s\\xA0]+", " ").
				replaceAll("\\s*[–\\-]\\s*", "-").
				trim();
	}
	
}
