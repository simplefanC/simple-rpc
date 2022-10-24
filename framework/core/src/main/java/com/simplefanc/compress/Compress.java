package com.simplefanc.compress;

import com.simplefanc.extension.SPI;

@SPI
public interface Compress {
    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}
