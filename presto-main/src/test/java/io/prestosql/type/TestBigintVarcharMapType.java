/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.type;

import com.google.common.collect.ImmutableMap;
import io.prestosql.spi.block.Block;
import io.prestosql.spi.block.BlockBuilder;
import io.prestosql.spi.type.Type;
import org.testng.annotations.Test;

import java.util.Map;

import static io.prestosql.spi.type.BigintType.BIGINT;
import static io.prestosql.spi.type.VarcharType.VARCHAR;
import static io.prestosql.util.StructuralTestUtil.mapBlockOf;
import static io.prestosql.util.StructuralTestUtil.mapType;
import static org.testng.Assert.assertEquals;

public class TestBigintVarcharMapType
        extends AbstractTestType
{
    public TestBigintVarcharMapType()
    {
        super(mapType(BIGINT, VARCHAR), Map.class, createTestBlock(mapType(BIGINT, VARCHAR)));
    }

    public static Block createTestBlock(Type mapType)
    {
        BlockBuilder blockBuilder = mapType.createBlockBuilder(null, 2);
        mapType.writeObject(blockBuilder, mapBlockOf(BIGINT, VARCHAR, ImmutableMap.of(1, "hi")));
        mapType.writeObject(blockBuilder, mapBlockOf(BIGINT, VARCHAR, ImmutableMap.of(1, "2", 2, "hello")));
        return blockBuilder.build();
    }

    @Test
    public void testValidateData()
    {
        final Block testBlock = createTestBlock(mapType(BIGINT, VARCHAR));

        Map<Long, String> valMap = ((Map<Long, String>) mapType(BIGINT, VARCHAR).getObjectValue(null, testBlock, 0));
        assertEquals("hi", valMap.get(1L));

        valMap = ((Map<Long, String>) mapType(BIGINT, VARCHAR).getObjectValue(null, testBlock, 1));
        assertEquals("2", valMap.get(1L));
        assertEquals("hello", valMap.get(2L));
    }

    @Override
    protected Object getGreaterValue(Object value)
    {
        throw new UnsupportedOperationException();
    }
}
