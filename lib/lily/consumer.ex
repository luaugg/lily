defmodule Lily.Consumer do
  use Nostrum.Consumer

  alias Nostrum.Api

  def start_link, do: Consumer.start_link(__MODULE__)

  def handle_event({:MESSAGE_CREATE, msg = %{content: "l->ping"}, _ws_state}), do:
    Api.create_message(msg.channel_id, "pong!")

  def handle_event(_event), do: :noop
end