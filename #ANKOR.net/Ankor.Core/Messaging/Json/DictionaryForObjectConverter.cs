﻿using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using Newtonsoft.Json;

namespace Ankor.Core.Messaging.Json {
	public class DictionaryForObjectConverter : JsonConverter {
		/// <summary>
		/// Writes the JSON representation of the object.
		/// </summary>
		/// <param name="writer">The <see cref="JsonWriter"/> to write to.</param>
		/// <param name="value">The value.</param>
		/// <param name="serializer">The calling serializer.</param>
		public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer) {
			// can write is set to false
		}

		/// <summary>
		/// Reads the JSON representation of the object.
		/// </summary>
		/// <param name="reader">The <see cref="JsonReader"/> to read from.</param>
		/// <param name="objectType">Type of the object.</param>
		/// <param name="existingValue">The existing value of object being read.</param>
		/// <param name="serializer">The calling serializer.</param>
		/// <returns>The object value.</returns>
		public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer) {
			return ReadValue(reader);
		}

		private object ReadValue(JsonReader reader) {
			while (reader.TokenType == JsonToken.Comment) {
				if (!reader.Read())
					throw new JsonSerializationException("Unexpected end when reading " + reader.Path);
			}

			switch (reader.TokenType) {
				case JsonToken.StartObject:
					return ReadObject(reader);
				case JsonToken.StartArray:
					return ReadList(reader);
				default:
					if (IsPrimitiveToken(reader.TokenType))
						return reader.Value;

					throw new JsonSerializationException(
						string.Format("Unexpected token when converting {0} at {1}", reader.TokenType, reader.Path));
			}
		}

		internal static bool IsPrimitiveToken(JsonToken token) {
			switch (token) {
				case JsonToken.Integer:
				case JsonToken.Float:
				case JsonToken.String:
				case JsonToken.Boolean:
				case JsonToken.Undefined:
				case JsonToken.Null:
				case JsonToken.Date:
				case JsonToken.Bytes:
					return true;
				default:
					return false;
			}
		}


		private object ReadList(JsonReader reader) {
			IList<object> list = new List<object>();

			while (reader.Read()) {
				switch (reader.TokenType) {
					case JsonToken.Comment:
						break;
					default:
						object v = ReadValue(reader);

						list.Add(v);
						break;
					case JsonToken.EndArray:
						return list;
				}
			}

			throw new JsonSerializationException("Unexpected end when reading at " + reader.Path);
		}

		private object ReadObject(JsonReader reader) {
			IDictionary<string, object> dict = new Dictionary<string, object>();

			while (reader.Read()) {
				switch (reader.TokenType) {
					case JsonToken.PropertyName:
						string propertyName = reader.Value.ToString();

						if (!reader.Read())
							throw new JsonSerializationException("Unexpected end when reading at " + reader.Path);

						object v = ReadValue(reader);

						dict[propertyName] = v;
						break;
					case JsonToken.Comment:
						break;
					case JsonToken.EndObject:
						return dict;
				}
			}

			throw new JsonSerializationException("Unexpected end when reading at " + reader.Path);
		}

		/// <summary>
		/// Determines whether this instance can convert the specified object type.
		/// </summary>
		/// <param name="objectType">Type of the object.</param>
		/// <returns>
		/// 	<c>true</c> if this instance can convert the specified object type; otherwise, <c>false</c>.
		/// </returns>
		public override bool CanConvert(Type objectType) {
			return (objectType == typeof (object));
		}

		/// <summary>
		/// Gets a value indicating whether this <see cref="JsonConverter"/> can write JSON.
		/// </summary>
		/// <value>
		/// 	<c>true</c> if this <see cref="JsonConverter"/> can write JSON; otherwise, <c>false</c>.
		/// </value>
		public override bool CanWrite {
			get { return false; }
		}
	}
}

