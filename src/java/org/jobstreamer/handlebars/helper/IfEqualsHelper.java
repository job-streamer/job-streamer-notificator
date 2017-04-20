package org.jobstreamer.handlebars.helper;

import java.io.IOException;
import java.util.Objects;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Options.Buffer;

/**
 * You can use the ifequals helper to conditionally render a block. If its first
 * argument and second argument are not equal, Handlebars will not render the block.
 *
 * @author tsutusmi kazuki
 * @since 1.0.1
 */
public class IfEqualsHelper implements Helper<Object> {

  /**
   * A singleton instance of this helper.
   */
  public static final Helper<Object> INSTANCE = new IfEqualsHelper();

  /**
   * The helper's name.
   */
  public static final String NAME = "ifequals";

  @Override
  public CharSequence apply(final Object context, final Options options)
      throws IOException {
    Buffer buffer = options.buffer();
    String target = (String) options.param(0, null);

    if (Objects.equals(context, target)) {
      buffer.append(options.fn());
    } else {
      buffer.append(options.inverse());
    }
    return buffer;
  }
}