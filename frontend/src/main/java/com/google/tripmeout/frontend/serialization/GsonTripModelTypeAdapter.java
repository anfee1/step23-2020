package com.google.tripmeout.frontend.serialization;

import com.google.common.base.Strings;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.tripmeout.frontend.TripModel;
import java.io.IOException;

public class GsonTripModelTypeAdapter extends TypeAdapter<TripModel> {
  private static final String ID_JSON_FIELD_NAME = "id";
  private static final String NAME_JSON_FIELD_NAME = "name";
  private static final String USER_ID_JSON_FIELD_NAME = "userId";
  private static final String PLACES_API_PLACE_ID_FIELD_NAME = "placesApiPlaceId";

  @Override
  public TripModel read(JsonReader reader) throws IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    }
    TripModel.Builder tripBuilder = TripModel.builder();
    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      switch (name) {
        case ID_JSON_FIELD_NAME:
          tripBuilder.setId(reader.nextString());
          break;
        case NAME_JSON_FIELD_NAME:
          tripBuilder.setName(reader.nextString());
          break;
        case USER_ID_JSON_FIELD_NAME:
          tripBuilder.setUserId(reader.nextString());
          break;
        case PLACES_API_PLACE_ID_FIELD_NAME:
          tripBuilder.setPlacesApiPlaceId(reader.nextString());
          break;
        default:
          throw new JsonParseException(
              String.format("Unknown field name %s for type Trip Model", name));
      }
    }
    reader.endObject();
    return tripBuilder.build();
  }

  @Override
  public void write(JsonWriter writer, TripModel trip) throws IOException {
    if (trip == null) {
      writer.nullValue();
      return;
    }
    writer.beginObject();
    writeUnlessNullOrEmpty(writer, ID_JSON_FIELD_NAME, trip.id());
    writeUnlessNullOrEmpty(writer, NAME_JSON_FIELD_NAME, trip.name());
    writeUnlessNullOrEmpty(writer, USER_ID_JSON_FIELD_NAME, trip.userId());
    writeUnlessNullOrEmpty(writer, PLACES_API_PLACE_ID_FIELD_NAME, trip.placesApiPlaceId());
    writer.endObject();
  }

  private static void writeUnlessNullOrEmpty(JsonWriter writer, String name, String value)
      throws IOException {
    if (!Strings.isNullOrEmpty(value)) {
      writer.name(name);
      writer.value(value);
    }
  }
}
