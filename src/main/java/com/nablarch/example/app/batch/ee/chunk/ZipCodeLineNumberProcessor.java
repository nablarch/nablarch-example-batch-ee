package com.nablarch.example.app.batch.ee.chunk;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

import com.nablarch.example.app.batch.ee.dto.fixedlength.ZipCodeDto;

/**
 * 郵便番号の行番号を取得する{@link ItemProcessor}実装クラス。
 *
 * @author Nabu Rakutaro
 */
@Dependent
@Named
public class ZipCodeLineNumberProcessor implements ItemProcessor {

    /** 行番号 */
    private Long lineNumber = 1L;

    @Override
    public Object processItem(Object item) {
        final ZipCodeDto dto = (ZipCodeDto) item;
        dto.setLineNumber(lineNumber);
        lineNumber++;
        return item;
    }
}
