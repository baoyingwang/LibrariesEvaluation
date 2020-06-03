package baoying.eval.jdk.util;
/*
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.NodeDetail;
*/
import org.xml.sax.SAXException;

//comment everything - 因为dependency问题，需要重新考虑这个package放在哪里

public class XMLDiff {
/*
	private static Logger LOGGER = Logger.getLogger(XMLDiff.class);



	public void compare(String liveHome,
			String expectHome, String[] xmlfiles) throws Exception {

		for (String xmlfile : xmlfiles) {

			String live = liveHome + xmlfile;
			String expect = expectHome + xmlfile;

			checkExist(live);
			checkExist(expect);
			DiffResult result = isDifferentXMLFile(live, expect);

			LOGGER.info("========================begin");
			if (result.isDiff) {
				LOGGER.error("different between " + live + " and " + expect);
				LOGGER.error("detail: " + result.desc);
			} else {
				LOGGER.info("same between " + live + " and " + expect);
			}
			LOGGER.info("========================end");
		}

	}

	private static void checkExist(String f) {

		File file = new File(f);
		if (file.isDirectory() || !file.exists()) {
			throw new RuntimeException(
					"File does not exist, or it is an directory:"
							+ file.getAbsolutePath());
		}

	}

	public DiffResult differentXML(String controlXML, String testXML)
			throws SAXException, IOException {

		DetailedDiff xmlDiff = new DetailedDiff(new Diff(controlXML, testXML));

		List<Difference> differences = xmlDiff.getAllDifferences();

		String allDesc = "";
		boolean isDiff = false;
		for (Difference diff : differences) {
			// dummy code. it is used to tell reader the two method:
			// getControlNodeDetail, and getTestNodeDetail
			NodeDetail expectedDetail = diff.getControlNodeDetail();
			NodeDetail actualDetail = diff.getTestNodeDetail();

			if (isEmpty(diff.getControlNodeDetail().getValue())
					&& isEmpty(diff.getTestNodeDetail().getValue())) {
				// ignore
				continue;
			}

			{
				String thisDesc = "Incorrect "
						+ diff.getTestNodeDetail().getXpathLocation()
						+ " value. Expected "
						+ diff.getControlNodeDetail().getValue() + " received "
						+ diff.getTestNodeDetail().getValue();
				allDesc = allDesc + "\n" + thisDesc;
				// LOGGER.error(thisDesc);

			}

			isDiff = true;
		}

		if (differences.size() > 0) {
			return new DiffResult(isDiff, allDesc);
		} else {
			return new DiffResult(isDiff, allDesc);
		}

	}

	public DiffResult isDifferentXMLFile(String xmlFile1, String xmlFile2)
			throws SAXException, IOException {

		return differentXML(FileReaderUtil.read(xmlFile1),
				FileReaderUtil.read(xmlFile2));

	}

	private boolean isEmpty(String value) {
		if (value == null || value.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	class DiffResult {
		final boolean isDiff;
		final String desc;

		DiffResult(boolean isDiff, String desc) {
			this.isDiff = isDiff;
			this.desc = desc;
		}

	}
	
	public static void main(String[] args) throws Exception {

		String[] xmlfiles = new String[] { "ttalert.xml", "ttconnect.xml" };

		String myDownloadedFIXGW2_8_5_1Home = "D:/Download/XYZFixGateway-2.8.5.1_5/";
		String myReleaseProjectHome = "D:/baoying.wang/2012/git_repository/ReleaseProject/";
		String[] liveFixConfigHomes = new String[] {
				myDownloadedFIXGW2_8_5_1Home
						+ "TCMInstance_assembly-2.8.5.1_5/data/config/",
				myDownloadedFIXGW2_8_5_1Home
						+ "TCMInstance_xxx_production-2.8.5.1_5/data/config/",
				myDownloadedFIXGW2_8_5_1Home
						+ "TCMInstance_zzz_uat-2.8.5.1_5/data/config/",
				myDownloadedFIXGW2_8_5_1Home
						+ "TCMInstance_yyy_production-2.8.5.1_5/data/config/",

		};
		String[] expectFixConfigHomes = new String[] {
				myReleaseProjectHome
						+ "ExpectedConfiguration/assembly.config/TCMRelease/data/config/",
				myReleaseProjectHome
						+ "ExpectedConfiguration/productionXXX.config/TCMRelease/data/config/",
				myReleaseProjectHome
						+ "ExpectedConfiguration/uat.config/TCMRelease/data/config/",
				myReleaseProjectHome
						+ "ExpectedConfiguration/productionYYY.config/TCMRelease/data/config/", };

		for (int i = 0; i < liveFixConfigHomes.length; i++) {
			new XMLDiff().compare(liveFixConfigHomes[i], expectFixConfigHomes[i], xmlfiles);
		}

	}
	*/
}
