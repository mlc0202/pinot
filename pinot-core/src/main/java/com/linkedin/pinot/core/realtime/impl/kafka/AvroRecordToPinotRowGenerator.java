/**
 * Copyright (C) 2014-2015 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkedin.pinot.core.realtime.impl.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Array;
import org.apache.avro.util.Utf8;

import com.linkedin.pinot.common.data.Schema;
import com.linkedin.pinot.core.data.GenericRow;
import com.linkedin.pinot.core.data.readers.AvroRecordReader;


public class AvroRecordToPinotRowGenerator {
  private final Schema indexingSchema;

  public AvroRecordToPinotRowGenerator(Schema indexingSchema) {
    this.indexingSchema = indexingSchema;
  }

  public GenericRow transform(GenericData.Record record) {
    Map<String, Object> rowEntries = new HashMap<String, Object>();
    for (String column : indexingSchema.getColumnNames()) {
      Object entry = record.get(column);
      if (entry instanceof Utf8) {
        entry = ((Utf8) entry).toString();
      }
      if (entry instanceof Array) {
        entry = AvroRecordReader.transformAvroArrayToObjectArray((Array) entry);
      }
      rowEntries.put(column, entry);
    }

    GenericRow row = new GenericRow();
    row.init(rowEntries);
    return row;
  }
}