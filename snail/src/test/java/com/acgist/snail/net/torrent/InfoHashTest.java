package com.acgist.snail.net.torrent;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.acgist.snail.net.DownloadException;
import com.acgist.snail.utils.Performance;

class InfoHashTest extends Performance {

	@Test
	void testToString() throws DownloadException {
		final InfoHash infoHash = InfoHash.newInstance("1".repeat(40));
		assertNotNull(infoHash);
		this.log(infoHash);
	}
	
}
